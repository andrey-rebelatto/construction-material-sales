package br.edu.materialconstrucao.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Aplicação acadêmica sem login; o Spring Security mantém a proteção CSRF nos formulários.
    return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .formLogin(login -> login.disable())
        .httpBasic(basic -> basic.disable())
        .build();
  }
}
