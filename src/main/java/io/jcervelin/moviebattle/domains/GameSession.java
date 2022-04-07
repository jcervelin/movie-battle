package io.jcervelin.moviebattle.domains;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSession {
  private String sessionId;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private Integer score;
  private Integer numErrors;

  public void incrementScore() {
    this.score++;
  }

  public void incrementNumErrors() {
    this.numErrors++;
  }

  public Integer absoluteScore() {
    return this.score - this.numErrors;
  }
}
