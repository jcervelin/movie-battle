package io.jcervelin.moviebattle.configs;

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

  /**
   * Method to set paths to be included through swagger
   *
   * @return Docket
   */
  @Bean
  public Docket configApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .securityContexts(List.of(securityContext()))
        .securitySchemes(List.of(apiKey()))
        .pathMapping("/")
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(regex("/api.*"))
        .build();
  }

  /**
   * Method to set swagger info
   *
   * @return ApiInfoBuilder
   */
  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Movie Battle")
        .description(
            "This Api enables user to start a game, end a game, get a question and answer the question. \n"
                + "There are 198 preloaded movies, so it's optional to load new movies. Also an API key is required.")
        .version("1.0")
        .build();
  }

  private ApiKey apiKey() {
    return new ApiKey("JWT", "Authorization", "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder().securityReferences(defaultAuth()).build();
  }

  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
  }
}
