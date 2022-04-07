package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.AnswerRequest;
import io.jcervelin.moviebattle.domains.AnswerResponse;
import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.Movie;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerChecker {

  private final PlayDecider playDecider;
  private final GameSessionManagement gameSessionManagement;
  private final Clock clock;

  @Transactional
  public AnswerResponse checkAnswer(AnswerRequest request) {

    // Chama PlayDecider para receber o filme com maior score * votos
    var winner = playDecider.theWinnerIs(request.getSessionId());

    // Recuperar sessao do jogo
    var gameSession = gameSessionManagement.findSession(request.getSessionId());

    // Adiciona 1 ponto ao score ou ao numero de erros, dependendo da escolha do usuario
    applyScoreOrErrorToEntity(request, gameSession, winner);

    // Verifica se deve encerrar o jogo
    shouldEndGame(gameSession);

    // atualiza a sessao
    gameSessionManagement.update(gameSession);

    return createAnswerResponse(gameSession);
  }

  private void shouldEndGame(GameSession gameSession) {
    if (playDecider.isEndGame(gameSession.getNumErrors())) {
      gameSession.setEndedAt(LocalDateTime.now(clock));
    }
  }

  private void applyScoreOrErrorToEntity(
      AnswerRequest request, GameSession gameSession, Movie winner) {
    if (winner.isCorrect(request.getAnswer())) {
      gameSession.incrementScore();
    } else {
      gameSession.incrementNumErrors();
    }
  }

  private AnswerResponse createAnswerResponse(GameSession gameSession) {
    return AnswerResponse.builder()
        .score(gameSession.getScore())
        .numErrors(gameSession.getNumErrors())
        .endGame(Objects.nonNull(gameSession.getEndedAt()))
        .sessionId(gameSession.getSessionId())
        .build();
  }
}
