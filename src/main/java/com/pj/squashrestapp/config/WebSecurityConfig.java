package com.pj.squashrestapp.config;

import com.pj.squashrestapp.config.security.accessexceptionhandler.AccessDeniedExceptionHandler;
import com.pj.squashrestapp.config.security.accessexceptionhandler.AuthenticationExceptionHandler;
import com.pj.squashrestapp.config.security.token.JwtAuthenticationFilter;
import com.pj.squashrestapp.config.security.token.JwtAuthorizationFilter;
import com.pj.squashrestapp.config.security.token.SecretKeyHolder;
import com.pj.squashrestapp.hexagonal.email.SendEmailFacade;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.service.TokenCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
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
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/** */
@Configuration
@EnableWebSecurity
@EnableWebMvc
@ComponentScan
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;
  private final PlayerRepository playerRepository;
  private final TokenCreateService tokenCreateService;
  private final SecretKeyHolder secretKeyHolder;
  private final SendEmailFacade sendEmailFacade;


  @Override
  protected void configure(final HttpSecurity httpSecurity) throws Exception {

    // disables cross-origin-resource-sharing and cross-site-request-forgery protection
    httpSecurity.cors().and().csrf().disable();

    // set up of endpoints permissions
    httpSecurity
        .authorizeRequests()
        // allowing not authenticated players view scoreboard for rounds and seasons
        .antMatchers(HttpMethod.GET, "/scoreboards/seasons/*")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/scoreboards/rounds/*")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/league-logos/season/*")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/league-logos/round/*")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/league-logos/*")
        .permitAll()
        // allowing to initialize the database
        .antMatchers(HttpMethod.POST, "/init/json")
        .permitAll()
        // allowing regular endpoints to be accessible
        .antMatchers(HttpMethod.GET, "/access/reset-password-player/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/access/refresh-token/*")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/access/sign-up")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/access/request-password-reset")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/access/request-magic-login-link")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/access/login-with-magic-link")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/access/confirm-password-reset")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/access/confirm-email-change")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/access/confirm-registration")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/login")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/players/name-taken/*")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/contact-form/send")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/frontend-logs")
        .permitAll()
        .anyRequest()
        .authenticated();

    // authentication and authorization filters
    httpSecurity
        .addFilter(
            new JwtAuthenticationFilter(
                authenticationManager(), tokenCreateService, playerRepository, sendEmailFacade))
        .addFilter(
            new JwtAuthorizationFilter(authenticationManager(), secretKeyHolder, playerRepository));

    // this disables session creation on Spring Security
    httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // exception handling for unauthenticated/unathorized accesses
    httpSecurity
        .exceptionHandling()
        .authenticationEntryPoint(AuthenticationExceptionHandler::new)
        .accessDeniedHandler(AccessDeniedExceptionHandler::new);
  }

  @Override
  public void configure(final WebSecurity webSecurity) throws Exception {
    webSecurity
        .ignoring()
        .mvcMatchers(HttpMethod.OPTIONS, "/**")
        .and()
        .ignoring()
        .mvcMatchers(
            "/swagger-ui.html/**",
            "/configuration/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/webjars/**");
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
    return new Argon2PasswordEncoder(16, 32, 1, 32768, 12);
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

  @Bean
  public ApplicationListener applicationListener() {
    return new AuthSuccessApplicationListener(sendEmailFacade);
  }
}
