package io.jcervelin.moviebattle.gateways.databases;

import io.jcervelin.moviebattle.domains.entities.GameHistoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameHistoryRepository extends JpaRepository<GameHistoryEntity, Integer> {

  @Query(
      "select g.id from GameHistoryEntity g where g.username = :username and g.sessionId = :sessionId and "
          + "((g.choice1.id = :choice1 and g.choice2.id = :choice2) or (g.choice1.id = :choice2 and g.choice2.id = :choice1))")
  Optional<Integer> hasUserAnsweredThisQuestion(
      @Param(value = "username") String username,
      @Param(value = "sessionId") String sessionId,
      @Param(value = "choice1") String choice1,
      @Param(value = "choice2") String choice2);

  @Override
  boolean existsById(Integer bigInteger);

  Optional<GameHistoryEntity> findByUsernameAndSessionIdAndAnswered(
      String username, String sessionId, boolean answered);

  boolean existsByUsernameAndSessionIdAndAnswered(
      String username, String sessionId, boolean answered);
}
