package io.jcervelin.moviebattle.usecases;

import static io.jcervelin.moviebattle.TestUtils.SESSION_ID;
import static io.jcervelin.moviebattle.TestUtils.SESSION_NOT_FOUND;
import static io.jcervelin.moviebattle.TestUtils.USER_NAME;
import static io.jcervelin.moviebattle.TestUtils.createGameSession;
import static io.jcervelin.moviebattle.TestUtils.createGameSessionEntity;
import static io.jcervelin.moviebattle.TestUtils.createGameSessionIdEntity;
import static io.jcervelin.moviebattle.TestUtils.mockClock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.entities.GameSessionEntity;
import io.jcervelin.moviebattle.domains.exceptions.SessionNotFoundException;
import io.jcervelin.moviebattle.gateways.databases.GameSessionRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GameSessionManagementTest {

  @InjectMocks private GameSessionManagement target;
  @Mock private GameSessionRepository gameSessionRepository;
  @Mock private LoggedUser loggedUser;
  @Mock private UUIDGenerator uuidGenerator;
  @Mock private Clock clock;

  @Test
  public void shouldCreateNewSession() {
    final LocalDateTime fixedNow = LocalDateTime.of(2021, 1, 1, 1, 1);
    mockClock(fixedNow, clock);
    when(uuidGenerator.generate()).thenReturn(SESSION_ID);
    when(loggedUser.getUsername()).thenReturn(USER_NAME);

    GameSessionEntity expectedEntity = createGameSessionEntity(0, 0, fixedNow, null);
    GameSession expected = createGameSession(0, 0, fixedNow, null);

    final GameSession result = target.createNew();

    verify(gameSessionRepository).save(expectedEntity);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void shouldEndSession() {
    final LocalDateTime fixedNow = LocalDateTime.of(2021, 1, 1, 1, 1);
    mockClock(fixedNow, clock);
    final LocalDateTime startDate = fixedNow.minusDays(1);
    when(loggedUser.getUsername()).thenReturn(USER_NAME);

    GameSessionEntity responseFromDb = createGameSessionEntity(0, 0, startDate, null);
    GameSessionEntity expectedEntity = createGameSessionEntity(0, 0, startDate, fixedNow);
    GameSession expected = createGameSession(0, 0, startDate, fixedNow);

    when(gameSessionRepository.findById(responseFromDb.getId()))
        .thenReturn(Optional.of(responseFromDb));

    final GameSession result = target.end(SESSION_ID);

    verify(gameSessionRepository).save(expectedEntity);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void shouldValidateExistingSession() {
    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameSessionRepository.existsByIdAndEndIsNull(createGameSessionIdEntity()))
        .thenReturn(true);
    final Boolean result = target.isValid(SESSION_ID);
    assertThat(result).isTrue();
  }

  @Test
  public void shouldValidateNonExistingSession() {
    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameSessionRepository.existsByIdAndEndIsNull(createGameSessionIdEntity()))
        .thenReturn(false);
    final Boolean result = target.isValid(SESSION_ID);
    assertThat(result).isFalse();
  }

  @Test
  public void shouldUpdateSessionSuccessfully() {
    final LocalDateTime startDate = LocalDateTime.of(2021, 1, 1, 1, 1);
    final LocalDateTime endDate = startDate.minusDays(1);
    final GameSession request = createGameSession(1, 1, endDate, startDate);
    final GameSessionEntity expectedEntity = createGameSessionEntity(1, 1, endDate, startDate);
    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    target.update(request);
    verify(gameSessionRepository).save(expectedEntity);
  }

  @Test
  public void shouldFindSessionSuccessfully() {
    final LocalDateTime startDate = LocalDateTime.of(2021, 1, 1, 1, 1);
    final LocalDateTime endDate = startDate.minusDays(1);
    final GameSession gameSession = createGameSession(1, 1, endDate, startDate);
    final GameSessionEntity expectedEntity = createGameSessionEntity(1, 1, endDate, startDate);
    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameSessionRepository.findById(expectedEntity.getId()))
        .thenReturn(Optional.of(expectedEntity));
    final GameSession result = target.findSession(SESSION_ID);
    assertThat(result).isEqualToComparingFieldByField(gameSession);
  }

  @Test
  public void shouldThrowErrorWhenSessionNotFound() {
    final LocalDateTime startDate = LocalDateTime.of(2021, 1, 1, 1, 1);
    final LocalDateTime endDate = startDate.minusDays(1);
    final GameSessionEntity expectedEntity = createGameSessionEntity(1, 1, endDate, startDate);
    when(loggedUser.getUsername()).thenReturn(USER_NAME);
    when(gameSessionRepository.findById(expectedEntity.getId())).thenReturn(Optional.empty());

    final SessionNotFoundException sessionNotFoundException =
        assertThrows(SessionNotFoundException.class, () -> target.findSession(SESSION_ID));

    assertThat(sessionNotFoundException.getMessage()).isEqualTo(SESSION_NOT_FOUND);
  }
}
