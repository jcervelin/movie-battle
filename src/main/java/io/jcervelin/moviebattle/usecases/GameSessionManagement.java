package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.entities.GameSessionEntity;
import io.jcervelin.moviebattle.domains.entities.GameSessionIdEntity;
import io.jcervelin.moviebattle.domains.exceptions.SessionNotFoundException;
import io.jcervelin.moviebattle.gateways.databases.GameSessionRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSessionManagement {

  private static final Integer INITIAL_VALUE = 0;

  private final GameSessionRepository gameSessionRepository;
  private final LoggedUser loggedUser;
  private final UUIDGenerator uuidGenerator;
  private final Clock clock;

  /**
   * It's considered a valid session when: it exists, it's linked to the logged user and it's not
   * ended, i.e. when ({@link GameSessionEntity#getEnd()} is not null)
   *
   * @param sessionId, together with context's username are they keys to check whether the session
   *     is valid or not
   * @return true if session exists and it's linked to logged user {@link
   *     GameSessionEntity#getEnd()} is not null
   */
  public boolean isValid(String sessionId) {
    var id = createIdObject(sessionId);
    return gameSessionRepository.existsByIdAndEndIsNull(id);
  }

  public void update(GameSession gameSession) {
    var gameSessionEntity = createGameSessionEntity(gameSession);
    gameSessionRepository.save(gameSessionEntity);
  }

  @Transactional
  public GameSession createNew() {
    var gameSessionEntity = createEntityObject();

    // Returns existing session, if any
    var gameSession =
        gameSessionRepository.hasAnyOpenSession(gameSessionEntity.getId().getUsername()).stream()
            .findFirst()
            .map(this::createGameSession)
            .orElse(saveNewAndReturn(gameSessionEntity));

    log.info("Session created: {}", gameSession);
    return gameSession;
  }

  private GameSession saveNewAndReturn(GameSessionEntity gameSessionEntity) {
    gameSessionRepository.save(gameSessionEntity);
    return createGameSession(gameSessionEntity);
  }

  @Transactional
  public GameSession end(String sessionId) {
    var gameSessionEntity = findSessionEntity(sessionId);
    gameSessionEntity.setEnd(LocalDateTime.now(clock));
    gameSessionRepository.save(gameSessionEntity);
    return createGameSession(gameSessionEntity);
  }

  public GameSession findSession(String sessionId) {
    var gameSessionEntity = findSessionEntity(sessionId);
    return createGameSession(gameSessionEntity);
  }

  private GameSessionEntity findSessionEntity(String sessionId) {
    var id = createIdObject(sessionId);
    return gameSessionRepository.findById(id).orElseThrow(SessionNotFoundException::new);
  }

  private GameSessionIdEntity createIdObject() {
    var sessionId = uuidGenerator.generate();
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
    var gameSessionIdEntity = createIdObject(gameSession.getSessionId());

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
