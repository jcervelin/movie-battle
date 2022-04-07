package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.entities.GameSessionEntity;
import io.jcervelin.moviebattle.domains.entities.GameSessionIdEntity;
import io.jcervelin.moviebattle.domains.exceptions.SessionNotFoundException;
import io.jcervelin.moviebattle.gateways.databases.GameSessionRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameSessionManagement {

  private static final Integer INITIAL_VALUE = 0;

  private final GameSessionRepository gameSessionRepository;
  private final LoggedUser loggedUser;
  private final UUIDGenerator uuidGenerator;
  private final Clock clock;

  /**
   * Uma sessao e considerada valida when ela existe, esta linkada ao usuario logado e nao esta
   * encerrada, ou seja, quando ({@link GameSessionEntity#getEnd()} nao esta nulo)
   *
   * @param sessionId a ser usada na query, juntamente com o username do contexto
   * @return true se sessao existe, esta linkada com usuario do contexto e {@link
   *     GameSessionEntity#getEnd()} nao esta nulo
   */
  public boolean isValid(String sessionId) {
    final GameSessionIdEntity id = createIdObject(sessionId);
    return gameSessionRepository.existsByIdAndEndIsNull(id);
  }

  public void update(GameSession gameSession) {
    var gameSessionEntity = createGameSessionEntity(gameSession);
    gameSessionRepository.save(gameSessionEntity);
  }

  public GameSession createNew() {
    final GameSessionEntity gameSessionEntity = createEntityObject();

    // Se o usuario tiver uma sessao aberta, retornar a mesma sessao
    return gameSessionRepository.hasAnyOpenSession(gameSessionEntity.getId().getUsername()).stream()
        .findFirst()
        .map(this::createGameSession)
        .orElse(saveNewAndReturn(gameSessionEntity));
  }

  private GameSession saveNewAndReturn(GameSessionEntity gameSessionEntity) {
    gameSessionRepository.save(gameSessionEntity);
    return createGameSession(gameSessionEntity);
  }

  public GameSession end(String sessionId) {
    final GameSessionEntity gameSessionEntity = findSessionEntity(sessionId);
    gameSessionEntity.setEnd(LocalDateTime.now(clock));
    gameSessionRepository.save(gameSessionEntity);
    return createGameSession(gameSessionEntity);
  }

  public GameSession findSession(String sessionId) {
    final GameSessionEntity gameSessionEntity = findSessionEntity(sessionId);
    return createGameSession(gameSessionEntity);
  }

  private GameSessionEntity findSessionEntity(String sessionId) {
    final GameSessionIdEntity id = createIdObject(sessionId);
    return gameSessionRepository.findById(id).orElseThrow(SessionNotFoundException::new);
  }

  private GameSessionIdEntity createIdObject() {
    final String sessionId = uuidGenerator.generate();
    return createIdObject(sessionId);
  }

  private GameSessionIdEntity createIdObject(String sessionId) {
    return GameSessionIdEntity.builder()
        .username(loggedUser.getUsername())
        .sessionId(sessionId)
        .build();
  }

  private GameSessionEntity createEntityObject() {
    return GameSessionEntity.builder()
        .id(createIdObject())
        .start(LocalDateTime.now(clock))
        .score(INITIAL_VALUE)
        .numErrors(INITIAL_VALUE)
        .absoluteScore(INITIAL_VALUE)
        .build();
  }

  private GameSession createGameSession(GameSessionEntity gameSessionEntity) {
    return GameSession.builder()
        .sessionId(gameSessionEntity.getId().getSessionId())
        .startedAt(gameSessionEntity.getStart())
        .endedAt(gameSessionEntity.getEnd())
        .numErrors(gameSessionEntity.getNumErrors())
        .score(gameSessionEntity.getScore())
        .build();
  }

  private GameSessionEntity createGameSessionEntity(GameSession gameSession) {
    final GameSessionIdEntity gameSessionIdEntity = createIdObject(gameSession.getSessionId());

    return GameSessionEntity.builder()
        .id(gameSessionIdEntity)
        .start(gameSession.getStartedAt())
        .end(gameSession.getEndedAt())
        .numErrors(gameSession.getNumErrors())
        .score(gameSession.getScore())
        .absoluteScore(gameSession.absoluteScore())
        .build();
  }
}
