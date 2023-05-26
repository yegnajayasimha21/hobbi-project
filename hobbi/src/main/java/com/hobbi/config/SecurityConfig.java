package com.hobbi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hobbi.security.HobbieUserDetailsService;



@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final HobbieUserDetailsService hobbieUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(HobbieUserDetailsService hobbieUserDetailsService, PasswordEncoder passwordEncoder) {
        this.hobbieUserDetailsService = hobbieUserDetailsService;
        
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hobbieUserDetailsService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
       http.csrf() 
                .disable() 
                .authorizeRequests() 
                .antMatchers("/testing","/register", "/signup", "/authenticate", "/notification", "/password", 
                        "/configuration/security","/h2-console/**", "/login/**") 
                .permitAll() 
                .anyRequest() 
                .authenticated() 
                .and() 
                .sessionManagement() 
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }
}
