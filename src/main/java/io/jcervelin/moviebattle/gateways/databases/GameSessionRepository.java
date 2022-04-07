package io.jcervelin.moviebattle.gateways.databases;

import io.jcervelin.moviebattle.domains.entities.GameSessionEntity;
import io.jcervelin.moviebattle.domains.entities.GameSessionIdEntity;
import io.jcervelin.moviebattle.domains.entities.IHallPositionProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameSessionRepository
    extends JpaRepository<GameSessionEntity, GameSessionIdEntity> {
  boolean existsByIdAndEndIsNull(GameSessionIdEntity id);

  @Query(
      value =
          "select username, max(absolute_score) absoluteScore from "
              + "tb_game_session group by username order by absoluteScore desc limit 10;",
      nativeQuery = true)
  List<IHallPositionProjection> findTop10Players();

  @Query(
      value =
          "select gs from GameSessionEntity gs where "
              + "gs.id.username = :username and gs.end is null")
  List<GameSessionEntity> hasAnyOpenSession(@Param("username") String username);
}
