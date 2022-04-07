package io.jcervelin.moviebattle.domains;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class BattleSecurityProperties {
  private Map<String, List<String>> routes;
}
