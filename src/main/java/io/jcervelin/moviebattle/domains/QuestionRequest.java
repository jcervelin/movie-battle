package io.jcervelin.moviebattle.domains;

import io.jcervelin.moviebattle.validators.ValidateSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequest {
  @ValidateSession private String sessionId;
}
