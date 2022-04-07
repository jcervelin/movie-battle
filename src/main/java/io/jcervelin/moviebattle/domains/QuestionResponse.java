package io.jcervelin.moviebattle.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponse {

  private MovieChoice choice1;
  private MovieChoice choice2;
}
