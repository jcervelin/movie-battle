package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.BattleProperties;
import io.jcervelin.moviebattle.domains.Movie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlayDecider {

  private final BattleProperties properties;
  private final GameHistoryManagement gameHistoryManagement;

  public boolean isEndGame(Integer numErrors) {
    return numErrors >= properties.getMaxErrors();
  }

  public Movie theWinnerIs(String session) {

    // Recuperar questao aberta linkada com a sessao no request
    var moviePair = gameHistoryManagement.getOpenedQuestion(session);

    final Movie choice1 = moviePair.getChoice1();
    final Movie choice2 = moviePair.getChoice2();

    var movieFactor1 = choice1.getImdbRating().multiply(choice1.getImdbVotes());
    var movieFactor2 = choice2.getImdbRating().multiply(choice2.getImdbVotes());

    var winner = movieFactor1.compareTo(movieFactor2) > 0 ? choice1 : choice2;
    log.info("The winner is: {}", winner);

    // marcar questao como respondida
    gameHistoryManagement.markAsAnswered(session);

    return winner;
  }
}
