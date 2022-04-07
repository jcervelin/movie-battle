package io.jcervelin.moviebattle.domains.entities;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class GameSessionIdEntity implements Serializable {
  private String username;
  private String sessionId;
}
