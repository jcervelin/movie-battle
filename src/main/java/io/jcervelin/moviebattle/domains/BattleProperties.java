package io.jcervelin.moviebattle.domains;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component
@Data
public class BattleProperties {
  private int maxErrors;
  private BattleSecurityProperties security;
}
