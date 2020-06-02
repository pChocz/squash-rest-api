package com.pj.squashrestapp.config;

import com.pj.squashrestapp.config.security.JWTAuthenticationFilter;
import com.pj.squashrestapp.config.security.JWTAuthorizationFilter;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;

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

  @Override
  protected void configure(final HttpSecurity http) throws Exception {

    http.cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(new JWTAuthenticationFilter(authenticationManager()))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), userDetailsService))
            // this disables session creation on Spring Security
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http
            .exceptionHandling()
            .authenticationEntryPoint((request, response, e) ->
            {
              response.setContentType("application/json;charset=UTF-8");
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              try (final PrintWriter writer = response.getWriter()) {
                writer.write(new JSONObject()
                        .appendField("response", HttpServletResponse.SC_FORBIDDEN)
                        .appendField("user", "ANONYMOUS USER")
                        .appendField("timestamp", new Date(System.currentTimeMillis()))
                        .appendField("message", "NOT AUTHENTICATED")
                        .toString());
              }
            })
            .accessDeniedHandler((request, response, e) ->
            {
              final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) request.getUserPrincipal();
              final String username = token.getName();
              final String[] authorities = token.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);

              response.setContentType("application/json;charset=UTF-8");
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              try (final PrintWriter writer = response.getWriter()) {
                writer.write(new JSONObject()
                        .appendField("response", HttpServletResponse.SC_FORBIDDEN)
                        .appendField("user", username)
                        .appendField("authorities", authorities)
                        .appendField("timestamp", new Date(System.currentTimeMillis()))
                        .appendField("message", "FORBIDDEN")
                        .toString());
              }
            });

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