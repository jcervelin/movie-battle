package io.jcervelin.moviebattle.gateways.databases;

import static io.jcervelin.moviebattle.TestUtils.createGameSessionEntities;
import static org.assertj.core.api.Assertions.assertThat;

import io.jcervelin.moviebattle.domains.entities.GameSessionEntity;
import io.jcervelin.moviebattle.domains.entities.IHallPositionProjection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class GameSessionRepositoryTest {

  @Autowired private GameSessionRepository target;

  @Test
  public void shouldReturnOnly10OrderedByAbsolutePosition() {
    final List<GameSessionEntity> entities = createGameSessionEntities(12);
    target.saveAll(entities);

    final List<IHallPositionProjection> top10Players = target.findTop10Players();

    assertThat(top10Players.size()).isEqualTo(10);
    assertThat(top10Players.get(0).getUsername()).isEqualTo("user12");
    assertThat(top10Players.get(0).getAbsoluteScore()).isEqualTo(12);
    assertThat(top10Players.get(9).getUsername()).isEqualTo("user3");
    assertThat(top10Players.get(9).getAbsoluteScore()).isEqualTo(3);
  }

  @Test
  public void shouldReturnGroupByMaximumScore() {
    final List<GameSessionEntity> gameSessionEntities = createGameSessionEntities(2);

    // user1Entity e user1EntityWithGreaterScoreAndDifferentSession ambos sao user1
    final GameSessionEntity user1Entity = gameSessionEntities.get(0);
    final GameSessionEntity user1EntityWithGreaterScoreAndDifferentSession =
        createGameSessionEntities(1).get(0);
    user1EntityWithGreaterScoreAndDifferentSession.setAbsoluteScore(3);
    user1EntityWithGreaterScoreAndDifferentSession.getId().setSessionId("some other session");

    final GameSessionEntity user2Entity = gameSessionEntities.get(1);

    target.saveAll(
        List.of(user1Entity, user2Entity, user1EntityWithGreaterScoreAndDifferentSession));

    // deve retornar apenas user1EntityWithGreaterScoreAndDifferentSession e user2Entity
    final List<IHallPositionProjection> top10Players = target.findTop10Players();

    assertThat(top10Players.size()).isEqualTo(2);
    assertThat(top10Players.get(0).getUsername()).isEqualTo("user1");
    assertThat(top10Players.get(0).getAbsoluteScore()).isEqualTo(3);
    assertThat(top10Players.get(1).getUsername()).isEqualTo("user2");
    assertThat(top10Players.get(1).getAbsoluteScore()).isEqualTo(2);
  }
}
