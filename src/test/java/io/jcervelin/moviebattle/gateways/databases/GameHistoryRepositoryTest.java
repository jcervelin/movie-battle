package io.jcervelin.moviebattle.gateways.databases;

import static io.jcervelin.moviebattle.TestUtils.SESSION_ID;
import static io.jcervelin.moviebattle.TestUtils.USER_NAME;
import static io.jcervelin.moviebattle.TestUtils.createGameHistoryEntity;
import static io.jcervelin.moviebattle.TestUtils.createMovieEntity;
import static org.assertj.core.api.Assertions.assertThat;

import io.jcervelin.moviebattle.domains.entities.GameHistoryEntity;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class GameHistoryRepositoryTest {

  @Autowired private GameHistoryRepository target;

  @Autowired private MovieRepository movieRepository;

  @Test
  public void whenUserHasAnsweredQuestionShouldReturnNotEmpty() {

    populateMovies(2);
    final GameHistoryEntity gameHistoryEntity = createGameHistoryEntity(1);
    target.save(gameHistoryEntity);
    final Optional<Integer> result =
        target.hasUserAnsweredThisQuestion(USER_NAME, SESSION_ID, "movieId1", "movieId2");
    assertThat(result).isNotEmpty();
  }

  @Test
  public void whenUserHasNotAnsweredQuestionShouldReturnEmpty() {

    populateMovies(4);
    final GameHistoryEntity gameHistoryEntity = createGameHistoryEntity(1);
    target.save(gameHistoryEntity);
    final Optional<Integer> result =
        target.hasUserAnsweredThisQuestion(USER_NAME, SESSION_ID, "movieId3", "movieId2");
    assertThat(result).isEmpty();
  }

  @Test
  public void whenUserHasAnsweredQuestionInDifferentOrderShouldReturnNotEmpty() {

    populateMovies(2);
    final GameHistoryEntity gameHistoryEntity = createGameHistoryEntity(1);
    target.save(gameHistoryEntity);
    final Optional<Integer> result =
        target.hasUserAnsweredThisQuestion(USER_NAME, SESSION_ID, "movieId2", "movieId1");
    assertThat(result).isNotEmpty();
  }

  private void populateMovies(int num) {
    IntStream.rangeClosed(1, num).forEach(i -> movieRepository.save(createMovieEntity(i)));
  }
}
