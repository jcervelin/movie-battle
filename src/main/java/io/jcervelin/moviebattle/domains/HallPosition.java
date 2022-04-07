package io.jcervelin.moviebattle.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HallPosition {

  private Integer place;
  private String username;
  private Integer absoluteScore;
}
