package io.jcervelin.moviebattle.domains;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Movie {
  private String id;
  private String title;
  private BigDecimal imdbRating;
  private BigDecimal imdbVotes;

  public boolean isCorrect(String answer) {
    return id.equals(answer);
  }
}
