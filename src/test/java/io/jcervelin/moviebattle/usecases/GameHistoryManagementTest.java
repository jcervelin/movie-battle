package io.jcervelin.moviebattle.usecases;

import static io.jcervelin.moviebattle.TestUtils.SESSION_ID;
import static io.jcervelin.moviebattle.TestUtils.SESSION_NOT_FOUND;
import static io.jcervelin.moviebattle.TestUtils.USER_NAME;
import static io.jcervelin.moviebattle.TestUtils.createGameHistoryEntity;
import static io.jcervelin.moviebattle.TestUtils.createMoviePair;
import static io.jcervelin.moviebattle.TestUtils.createMovies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.MoviePair;
import io.jcervelin.moviebattle.domains.entities.GameHistoryEntity;
import io.jcervelin.moviebattle.domains.exceptions.SessionNotFoundException;
import io.jcervelin.moviebattle.gateways.databases.GameHistoryRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GameHistoryManagementTest {

  @InjectMocks private GameHistoryManagement target;
  @Mock private GameHistoryRepository gameHistoryRepository;
  @Mock private LoggedUser loggedUser;
  @Mock private MovieMapCache movieMapCache;

  @Test
  public void existsByUsernameAndSessionIdAndAnsweredShouldReturnCorrectValue() {
    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameHistoryRepository.existsByUsernameAndSessionIdAndAnswered(
            USER_NAME, SESSION_ID, false))
        .thenReturn(true);

    assertThat(target.hasAnyOpenQuestion(SESSION_ID)).isTrue();
  }

  @Test
  public void hasUserAnsweredThisQuestionShouldReturnTrue() {

    final MoviePair moviePair = createMoviePair("movie1", "movie2");
    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameHistoryRepository.hasUserAnsweredThisQuestion(
            USER_NAME, SESSION_ID, "movie1", "movie2"))
        .thenReturn(Optional.of(1));
    assertThat(target.hasUserAnsweredThisQuestion(moviePair)).isTrue();
  }

  @Test
  public void hasUserAnsweredThisQuestionShouldReturnFalse() {

    final MoviePair moviePair = createMoviePair("movie1", "movie2");
    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameHistoryRepository.hasUserAnsweredThisQuestion(
            USER_NAME, SESSION_ID, "movie1", "movie2"))
        .thenReturn(Optional.empty());
    assertThat(target.hasUserAnsweredThisQuestion(moviePair)).isFalse();
  }

  @Test
  public void getOpenedQuestionShouldReturnSuccess() {
    final List<Movie> movies = createMovies(2);
    final MoviePair expectedMoviePair = createMoviePair(movies.get(0), movies.get(1));

    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameHistoryRepository.findByUsernameAndSessionIdAndAnswered(
            loggedUser.getUsername(), SESSION_ID, false))
        .thenReturn(Optional.of(createGameHistoryEntity(1)));

    when(movieMapCache.getMovies())
        .thenReturn(Map.of("movieId1", movies.get(0), "movieId2", movies.get(1)));

    final MoviePair result = target.getOpenedQuestion(SESSION_ID);

    assertThat(result).isEqualToComparingFieldByField(expectedMoviePair);
  }

  @Test
  public void getOpenedQuestionShouldThrowExceptionWhenGameHistoryNotFound() {
    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameHistoryRepository.findByUsernameAndSessionIdAndAnswered(
            loggedUser.getUsername(), SESSION_ID, false))
        .thenReturn(Optional.empty());

    var exception =
        assertThrows(SessionNotFoundException.class, () -> target.getOpenedQuestion(SESSION_ID));

    assertThat(exception.getMessage()).isEqualTo(SESSION_NOT_FOUND);
  }

  @Test
  public void shouldLinkQuestionToUserSuccessfully() {
    final List<Movie> movies = createMovies(2);
    final MoviePair moviePair = createMoviePair(movies.get(0), movies.get(1));

    final GameHistoryEntity gameHistoryEntity = createGameHistoryEntity(1);
    gameHistoryEntity.setId(null);

    when(loggedUser.getUsername()).thenReturn(USER_NAME);

    target.linkQuestionToUser(moviePair);

    verify(gameHistoryRepository).save(gameHistoryEntity);
  }

  @Test
  public void shouldMarkAsAnsweredSuccessfully() {
    final GameHistoryEntity gameHistoryEntity = createGameHistoryEntity(1);
    final GameHistoryEntity expectedGameHistoryEntity = createGameHistoryEntity(1);
    expectedGameHistoryEntity.setAnswered(true);

    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameHistoryRepository.findByUsernameAndSessionIdAndAnswered(
            loggedUser.getUsername(), SESSION_ID, false))
        .thenReturn(Optional.of(gameHistoryEntity));

    when(loggedUser.getUsername()).thenReturn(USER_NAME);

    target.markAsAnswered(SESSION_ID);

    verify(gameHistoryRepository).save(expectedGameHistoryEntity);
  }
}
