package io.jcervelin.moviebattle.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoviePair {
  private String sessionId;
  private Movie choice1;
  private Movie choice2;
}
