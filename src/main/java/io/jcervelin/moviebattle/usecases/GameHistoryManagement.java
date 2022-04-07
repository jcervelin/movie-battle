package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.MoviePair;
import io.jcervelin.moviebattle.domains.entities.GameHistoryEntity;
import io.jcervelin.moviebattle.domains.entities.MovieEntity;
import io.jcervelin.moviebattle.domains.exceptions.SessionNotFoundException;
import io.jcervelin.moviebattle.gateways.databases.GameHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameHistoryManagement {

  private final GameHistoryRepository gameHistoryRepository;
  private final LoggedUser loggedUser;
  private final MovieMapCache movieMapCache;

  public boolean hasUserAnsweredThisQuestion(MoviePair moviePair) {
    return gameHistoryRepository
        .hasUserAnsweredThisQuestion(
            loggedUser.getUsername(),
            moviePair.getSessionId(),
            moviePair.getChoice1().getId(),
            moviePair.getChoice2().getId())
        .isPresent();
  }

  public boolean hasAnyOpenQuestion(String sessionId) {
    return gameHistoryRepository.existsByUsernameAndSessionIdAndAnswered(
        loggedUser.getUsername(), sessionId, false);
  }

  public MoviePair getOpenedQuestion(String sessionId) {
    var gameHistoryEntity = getOpenedQuestionAsEntity(sessionId);
    return createMoviePair(gameHistoryEntity);
  }

  private GameHistoryEntity getOpenedQuestionAsEntity(String sessionId) {
    return gameHistoryRepository
        .findByUsernameAndSessionIdAndAnswered(loggedUser.getUsername(), sessionId, false)
        .orElseThrow(SessionNotFoundException::new);
  }

  private MoviePair createMoviePair(GameHistoryEntity gameHistoryEntity) {
    var choice1 = movieMapCache.getMovies().get(gameHistoryEntity.getChoice1().getId());
    var choice2 = movieMapCache.getMovies().get(gameHistoryEntity.getChoice2().getId());

    return MoviePair.builder()
        .sessionId(gameHistoryEntity.getSessionId())
        .choice1(choice1)
        .choice2(choice2)
        .build();
  }

  public void linkQuestionToUser(MoviePair moviePair) {
    final String username = loggedUser.getUsername();

    final GameHistoryEntity gameHistory =
        GameHistoryEntity.builder()
            .choice1(createMovieEntity(moviePair.getChoice1()))
            .choice2(createMovieEntity(moviePair.getChoice2()))
            .username(username)
            .answered(false)
            .sessionId(moviePair.getSessionId())
            .build();

    gameHistoryRepository.save(gameHistory);
  }

  private MovieEntity createMovieEntity(Movie movie) {
    return MovieEntity.builder()
        .id(movie.getId())
        .title(movie.getTitle())
        .imdbRating(movie.getImdbRating())
        .imdbVotes(movie.getImdbVotes())
        .build();
  }

  public void markAsAnswered(String sessionId) {
    var entity = getOpenedQuestionAsEntity(sessionId);
    entity.setAnswered(true);
    gameHistoryRepository.save(entity);
  }
}
