package io.jcervelin.moviebattle;

import static org.mockito.Mockito.when;

import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.MovieChoice;
import io.jcervelin.moviebattle.domains.MoviePair;
import io.jcervelin.moviebattle.domains.QuestionResponse;
import io.jcervelin.moviebattle.domains.entities.GameHistoryEntity;
import io.jcervelin.moviebattle.domains.entities.GameSessionEntity;
import io.jcervelin.moviebattle.domains.entities.GameSessionIdEntity;
import io.jcervelin.moviebattle.domains.entities.MovieEntity;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestUtils {

  public static final String SESSION_ID = "session123";
  public static final String USER_NAME = "user123";
  public static final String SESSION_NOT_FOUND = "Session not found or doesn't belong to this user";
  public static final String MOVIE_NOT_FOUND = "Movies not found";

  public static List<Movie> createMovies(int num) {
    return IntStream.rangeClosed(1, num)
        .boxed()
        .map(
            index ->
                Movie.builder()
                    .title("Test Name " + index)
                    .id("movieId" + index)
                    .imdbRating(BigDecimal.valueOf(index))
                    .imdbVotes(BigDecimal.valueOf(index * 100))
                    .build())
        .collect(Collectors.toList());
  }

  public static QuestionResponse createQuestionResponse(MoviePair expectedMoviePair) {
    return QuestionResponse.builder()
        .choice1(
            MovieChoice.builder()
                .id(expectedMoviePair.getChoice1().getId())
                .name(expectedMoviePair.getChoice1().getTitle())
                .build())
        .choice2(
            MovieChoice.builder()
                .id(expectedMoviePair.getChoice2().getId())
                .name(expectedMoviePair.getChoice2().getTitle())
                .build())
        .build();
  }

  public static MoviePair createMoviePair(Movie choice1, Movie choice2) {
    return MoviePair.builder().sessionId(SESSION_ID).choice1(choice1).choice2(choice2).build();
  }

  public static MoviePair createMoviePair(String choice1, String choice2) {
    return MoviePair.builder()
        .sessionId(SESSION_ID)
        .choice1(Movie.builder().id(choice1).build())
        .choice2(Movie.builder().id(choice2).build())
        .build();
  }

  public static GameHistoryEntity createGameHistoryEntity(Integer index) {
    return GameHistoryEntity.builder()
        .username(USER_NAME)
        .sessionId(SESSION_ID)
        .id(index)
        .answered(false)
        .choice1(createMovieEntity(1))
        .choice2(createMovieEntity(2))
        .build();
  }

  private static MovieEntity createMovieEntity(Integer index) {
    return MovieEntity.builder()
        .id("movieId" + index)
        .imdbRating(BigDecimal.ONE)
        .imdbVotes(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(index)))
        .title("Test Name " + index)
        .build();
  }

  public static GameSession createGameSession(
      int score, int numErrors, LocalDateTime startedAt, LocalDateTime endedAt) {
    return GameSession.builder()
        .sessionId(SESSION_ID)
        .score(score)
        .numErrors(numErrors)
        .startedAt(startedAt)
        .endedAt(endedAt)
        .build();
  }

  public static GameSessionIdEntity createGameSessionIdEntity(String sessionId, String username) {
    return GameSessionIdEntity.builder().sessionId(sessionId).username(username).build();
  }

  public static GameSessionIdEntity createGameSessionIdEntity() {
    return createGameSessionIdEntity(SESSION_ID, USER_NAME);
  }

  public static List<GameSessionEntity> createGameSessionEntities(Integer num) {
    return IntStream.rangeClosed(1, num)
        .boxed()
        .map(
            i ->
                createGameSessionEntity(
                    createGameSessionIdEntity("session" + i, "user" + i),
                    i,
                    0,
                    LocalDateTime.now(),
                    LocalDateTime.now()))
        .collect(Collectors.toList());
  }

  public static GameSessionEntity createGameSessionEntity(
      GameSessionIdEntity id,
      int score,
      int numErrors,
      LocalDateTime startedAt,
      LocalDateTime endedAt) {
    return GameSessionEntity.builder()
        .id(id)
        .score(score)
        .numErrors(numErrors)
        .start(startedAt)
        .end(endedAt)
        .absoluteScore(score - numErrors)
        .build();
  }

  public static GameSessionEntity createGameSessionEntity(
      int score, int numErrors, LocalDateTime startedAt, LocalDateTime endedAt) {
    return createGameSessionEntity(
        createGameSessionIdEntity(), score, numErrors, startedAt, endedAt);
  }

  public static MovieEntity createMovieEntity(int index) {
    return MovieEntity.builder()
        .id("movieId" + index)
        .imdbVotes(BigDecimal.ONE)
        .imdbRating(BigDecimal.ONE)
        .title("Movie " + index)
        .build();
  }

  public static void mockClock(LocalDateTime now, Clock clock) {
    ZoneId zoneId = ZoneId.of("UTC");
    var fixedClock = Clock.fixed(now.toInstant(zoneId.getRules().getOffset(now)), zoneId);
    when(clock.instant()).thenReturn(Instant.now(fixedClock));
    when(clock.getZone()).thenReturn(fixedClock.getZone());
  }
}
