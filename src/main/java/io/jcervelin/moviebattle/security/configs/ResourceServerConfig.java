package io.jcervelin.moviebattle.security.configs;

import io.jcervelin.moviebattle.domains.BattleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import static io.jcervelin.moviebattle.configs.PermitionTypes.ADMIN;
import static io.jcervelin.moviebattle.configs.PermitionTypes.OPERATOR;
import static io.jcervelin.moviebattle.configs.PermitionTypes.PUBLIC;

@Configuration
@EnableResourceServer
@RequiredArgsConstructor
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

  private final BattleProperties battleProperties;

  @Autowired private JwtTokenStore tokenStore;

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) {
    resources.tokenStore(tokenStore);
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    var routes = battleProperties.getSecurity().getRoutes();
    var publicPermissions = routes.get(PUBLIC.name()).toArray(String[]::new);
    var operatorPermissions = routes.get(OPERATOR.name()).toArray(String[]::new);
    var adminPermissions = routes.get(ADMIN.name()).toArray(String[]::new);

    http.authorizeRequests()
        .antMatchers(publicPermissions)
        .permitAll()
        .antMatchers(HttpMethod.GET, operatorPermissions)
        .hasAnyRole(OPERATOR.name(), ADMIN.name())
        .antMatchers(adminPermissions)
        .hasRole(ADMIN.name())
        .anyRequest()
        .authenticated();

    http.cors().configurationSource(corsConfigurationSource());
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    var corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
    corsConfiguration.setAllowedMethods(Arrays.asList("POST", "DELETE", "PUT", "GET", "PATCH"));
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }

  @Bean
  FilterRegistrationBean<CorsFilter> corsFilter() {

    var bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }
}
