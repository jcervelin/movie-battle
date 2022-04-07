package io.jcervelin.moviebattle.usecases;

import static io.jcervelin.moviebattle.TestUtils.SESSION_ID;
import static io.jcervelin.moviebattle.TestUtils.SESSION_NOT_FOUND;
import static io.jcervelin.moviebattle.TestUtils.createGameSession;
import static io.jcervelin.moviebattle.TestUtils.createMovies;
import static io.jcervelin.moviebattle.TestUtils.mockClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jcervelin.moviebattle.domains.AnswerRequest;
import io.jcervelin.moviebattle.domains.AnswerResponse;
import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.Movie;
import io.jcervelin.moviebattle.domains.exceptions.SessionNotFoundException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AnswerCheckerTest {

  @Mock private PlayDecider playDecider;
  @Mock private GameSessionManagement gameSessionManagement;
  @Mock private Clock clock;
  @Captor private ArgumentCaptor<GameSession> gameSessionCaptor;
  @Captor private ArgumentCaptor<Integer> numErrorUsedToEndGameCaptor;

  @InjectMocks private AnswerChecker target;

  @Test
  public void checkAnswerShouldBeCorrectAndUserShouldScore() {
    final LocalDateTime startedAt = LocalDateTime.now().minusDays(1);

    final AnswerRequest request =
        AnswerRequest.builder().answer("movieId2").sessionId(SESSION_ID).build();

    final GameSession gameSession = createGameSession(2, 1, startedAt, null);

    final List<Movie> movies = createMovies(2);

    createMocks(gameSession, movies.get(1), false);

    final AnswerResponse answerResponse = target.checkAnswer(request);

    verify(gameSessionManagement).update(gameSessionCaptor.capture());

    final GameSession updatedGameSession = gameSessionCaptor.getValue();
    assertThat(numErrorUsedToEndGameCaptor.getValue()).isEqualTo(1);
    assertThat(updatedGameSession.getScore()).isEqualTo(3);
    assertThat(updatedGameSession.getNumErrors()).isEqualTo(1);
    assertThat(updatedGameSession.getEndedAt()).isNull();
    assertThat(answerResponse.isEndGame()).isFalse();
  }

  @Test
  public void checkAnswerShouldBeWrongAndUserShouldGetErrorAndShouldEndGame() {
    final LocalDateTime fixedNow = LocalDateTime.of(2021, 1, 1, 1, 1);
    mockClock(fixedNow, clock);

    final LocalDateTime startedAt = fixedNow.minusDays(1);
    final LocalDateTime endedAt = fixedNow;

    final AnswerRequest request =
        AnswerRequest.builder().answer("movieId2").sessionId(SESSION_ID).build();

    final List<Movie> movies = createMovies(2);

    final GameSession gameSession = createGameSession(2, 2, startedAt, null);

    createMocks(gameSession, movies.get(0), true);

    final AnswerResponse answerResponse = target.checkAnswer(request);

    verify(gameSessionManagement).update(gameSessionCaptor.capture());

    final GameSession updatedGameSession = gameSessionCaptor.getValue();

    assertThat(numErrorUsedToEndGameCaptor.getValue()).isEqualTo(3);
    assertThat(updatedGameSession.getScore()).isEqualTo(2);
    assertThat(updatedGameSession.getNumErrors()).isEqualTo(3);
    assertThat(updatedGameSession.getEndedAt()).isEqualTo(endedAt);
    assertThat(answerResponse.isEndGame()).isTrue();
  }

  @Test
  public void whenPlayDeciderThrowsExceptionFlowShouldNotGoThrough() {
    final AnswerRequest request =
        AnswerRequest.builder().answer("movieId2").sessionId(SESSION_ID).build();

    SessionNotFoundException ex = new SessionNotFoundException();

    when(playDecider.theWinnerIs(SESSION_ID)).thenThrow(ex);

    var exception = assertThrows(SessionNotFoundException.class, () -> target.checkAnswer(request));

    assertThat(exception.getMessage()).isEqualTo(SESSION_NOT_FOUND);
    verify(gameSessionManagement, never()).findSession(anyString());
    verify(playDecider, never()).isEndGame(any());
    verify(gameSessionManagement, never()).update(any());
  }

  private void createMocks(GameSession gameSession, Movie winner, boolean isEndGame) {
    when(gameSessionManagement.findSession(SESSION_ID)).thenReturn(gameSession);
    when(playDecider.theWinnerIs(SESSION_ID)).thenReturn(winner);
    when(playDecider.isEndGame(numErrorUsedToEndGameCaptor.capture())).thenReturn(isEndGame);
  }
}
