package com.pj.squashrestapp.config;

import com.pj.squashrestapp.config.security.accessexceptionhandler.AccessDeniedExceptionHandler;
import com.pj.squashrestapp.config.security.accessexceptionhandler.AuthenticationExceptionHandler;
import com.pj.squashrestapp.config.security.token.JwtAuthenticationFilter;
import com.pj.squashrestapp.config.security.token.JwtAuthorizationFilter;
import com.pj.squashrestapp.config.security.token.SecretKeyHolder;
import com.pj.squashrestapp.repository.BlacklistedTokenRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 *
 */
@Configuration
@EnableWebSecurity
@EnableWebMvc
@ComponentScan
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  UserDetailsService userDetailsService;

  @Autowired
  BlacklistedTokenRepository blacklistedTokenRepository;

  @Autowired
  PlayerRepository playerRepository;

  @Autowired
  SecretKeyHolder secretKeyHolder;

  @Override
  protected void configure(final HttpSecurity httpSecurity) throws Exception {

    // disables cross-origin-resource-sharing and cross-site-request-forgery protection
    httpSecurity.cors().and().csrf().disable();

    // set up of endpoints permissions
    httpSecurity.authorizeRequests()
            .antMatchers(HttpMethod.POST, "/players/signUp").permitAll()
            .antMatchers(HttpMethod.GET, "/players/confirmRegistration**").permitAll()
            .antMatchers(HttpMethod.GET, "/players/resetPassword**").permitAll()
            .antMatchers(HttpMethod.GET, "/players/requestPasswordReset**").permitAll()
            .antMatchers(HttpMethod.POST, "/db-initializers/**").permitAll()
            .anyRequest().permitAll();

    // authentication and authorization filters
    httpSecurity.addFilter(new JwtAuthenticationFilter(authenticationManager(), secretKeyHolder))
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), userDetailsService, secretKeyHolder, blacklistedTokenRepository, playerRepository));

    // this disables session creation on Spring Security
    httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // exception handling for unauthenticated/unathorized accesses
    httpSecurity.exceptionHandling()
            .authenticationEntryPoint(AuthenticationExceptionHandler::new)
            .accessDeniedHandler(AccessDeniedExceptionHandler::new);
  }

  @Override
  public void configure(final WebSecurity webSecurity) throws Exception {
    webSecurity
            .ignoring().mvcMatchers(HttpMethod.OPTIONS, "/**")
            .and()
            .ignoring().mvcMatchers("/swagger-ui.html/**", "/configuration/**", "/swagger-resources/**", "/v2/api-docs", "/webjars/**");
  }

  @Bean
  public DaoAuthenticationProvider authProvider() {
    final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(BCryptVersion.$2A, 12);
  }

  @Override
  public void configure(final AuthenticationManagerBuilder builder) throws Exception {
    builder.userDetailsService(userDetailsService);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }

}