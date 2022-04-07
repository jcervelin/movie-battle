package io.jcervelin.moviebattle.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerResponse {
  private String sessionId;
  private int score;
  private int numErrors;
  private boolean endGame;
}
