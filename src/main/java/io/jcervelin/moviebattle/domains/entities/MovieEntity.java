package io.jcervelin.moviebattle.domains.entities;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tb_movie")
public class MovieEntity {

  @Id private String id;
  private BigDecimal imdbRating;
  private BigDecimal imdbVotes;
  private String title;
}
