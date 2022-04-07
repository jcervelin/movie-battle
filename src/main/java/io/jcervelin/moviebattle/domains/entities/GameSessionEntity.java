package io.jcervelin.moviebattle.domains.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "tb_game_session")
public class GameSessionEntity implements Serializable {

  @EmbeddedId private GameSessionIdEntity id;
  private LocalDateTime start;
  private LocalDateTime end;
  private Integer score;
  private Integer numErrors;
  private Integer absoluteScore;
}
