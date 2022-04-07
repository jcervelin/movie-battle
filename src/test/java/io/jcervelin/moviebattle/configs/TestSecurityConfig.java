package io.jcervelin.moviebattle.configs;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@TestConfiguration
@Order(HIGHEST_PRECEDENCE)
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
  }
}
