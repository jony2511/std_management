package com.example.demo.config;

import com.example.demo.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // Teacher only endpoints - CRUD for students, teachers, departments, courses
                        .requestMatchers("/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/students/create", "/students/delete/**").hasRole("TEACHER")
                        .requestMatchers("/teachers/**").hasRole("TEACHER")
                        .requestMatchers("/departments/**").hasRole("TEACHER")
                        .requestMatchers("/courses/create", "/courses/edit/**", "/courses/delete/**").hasRole("TEACHER")

                        // Student endpoints - can only view and update own profile
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/students/profile/**").hasAnyRole("TEACHER", "STUDENT")

                        // Common endpoints
                        .requestMatchers("/courses", "/courses/view/**").hasAnyRole("TEACHER", "STUDENT")
                        .requestMatchers("/students", "/students/view/**").hasAnyRole("TEACHER", "STUDENT")

                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }
}
