/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.config;

import com.mcc40.crud.jwt.JwtConfig;
import com.mcc40.crud.jwt.JwtSecretKey;
import com.mcc40.crud.jwt.JwtTokenVerifier;
import com.mcc40.crud.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.mcc40.crud.repositories.UserRepository;
import com.mcc40.crud.services.MyUserDetailsService;
import com.mcc40.crud.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    private final MyUserDetailsService userDetailsService;
    private final JwtSecretKey secretKey;
    private final JwtConfig jwtConfig;
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfiguration(MyUserDetailsService userDetailsService, 
            JwtSecretKey secretKey, 
            JwtConfig jwtConfig) {
        this.userDetailsService = userDetailsService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), 
                        jwtConfig, 
                        secretKey.getSecretKey(),
                        userRepository,
                        passwordEncoder
                ))
                .addFilterAfter(new JwtTokenVerifier(secretKey.getSecretKey(), jwtConfig), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/users/forgot-password/**").permitAll()
                .antMatchers("/api/users/reset-password/**").permitAll()
                .antMatchers("/api/users/verify/**").permitAll()
                .antMatchers("/api/users/register/**").permitAll()
                .antMatchers("/api/users/login/**").permitAll()
                .antMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/location/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/jobs/**").hasAnyRole("HR", "ADMIN")
                .antMatchers("api/employee/**").hasAnyRole("HR", "ADMIN")
                .antMatchers("/**").hasRole("ADMIN")
                .antMatchers("/login/**").permitAll()
                .anyRequest().authenticated();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

}
