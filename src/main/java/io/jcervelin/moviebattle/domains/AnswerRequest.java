package io.jcervelin.moviebattle.domains;

import io.jcervelin.moviebattle.validators.ValidateSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerRequest {
  @ValidateSession private String sessionId;
  private String answer;
}
