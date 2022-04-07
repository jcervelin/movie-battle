package io.jcervelin.moviebattle.domains.entities;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "tb_game_history")
public class GameHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String sessionId;
  private String username;

  @ManyToOne
  @JoinColumn(
      name = "choice1_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_history_to_choice_1"))
  private MovieEntity choice1;

  @ManyToOne
  @JoinColumn(
      name = "choice2_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_history_to_choice_2"))
  private MovieEntity choice2;

  private boolean answered;
}
