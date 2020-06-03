package com.pj.squashrestapp.config;

import com.pj.squashrestapp.config.security.JwtAuthenticationFilter;
import com.pj.squashrestapp.config.security.JwtAuthorizationFilter;
import com.pj.squashrestapp.config.security.SecretKeyHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static com.pj.squashrestapp.config.security.SecurityConstants.SIGN_UP_URL;

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
  SecretKeyHolder secretKeyHolder;

  @Override
  protected void configure(final HttpSecurity http) throws Exception {

    // disables cross-origin-resource-sharing and cross-site-request-forgery protection
    http.cors().and().csrf().disable();

    // set up of endpoints permissions
    http.authorizeRequests()
            .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
            .anyRequest().authenticated();

    // authentication and authorization filters
    http.addFilter(new JwtAuthenticationFilter(authenticationManager(), secretKeyHolder))
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), userDetailsService, secretKeyHolder));

    // this disables session creation on Spring Security
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // exception handling for unauthenticated/unathorized accesses
    http.exceptionHandling()
            .authenticationEntryPoint(AuthenticationExceptionHandler::new)
            .accessDeniedHandler(AccessDeniedExceptionHandler::new);
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
    return new BCryptPasswordEncoder();
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