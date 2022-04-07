package io.jcervelin.moviebattle.gateways.controllers;

import static io.jcervelin.moviebattle.TestUtils.SESSION_ID;
import static io.jcervelin.moviebattle.TestUtils.SESSION_NOT_FOUND;
import static io.jcervelin.moviebattle.TestUtils.createGameSession;
import static io.jcervelin.moviebattle.domains.EndpointConstants.API_MOVIE_BATTLE;
import static io.jcervelin.moviebattle.domains.EndpointConstants.CREATE_SESSION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import io.jcervelin.moviebattle.configs.TestSecurityConfig;
import io.jcervelin.moviebattle.domains.ErrorResponse;
import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.exceptions.SessionNotFoundException;
import io.jcervelin.moviebattle.usecases.GameSessionManagement;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestSecurityConfig.class)
public class SessionControllerTest {

  @Autowired private TestRestTemplate restTemplate;
  @MockBean private GameSessionManagement gameSessionManagement;

  @Test
  public void shouldThrowInternalServerErrorException() {
    when(gameSessionManagement.createNew()).thenThrow(new RuntimeException("Some error"));
    ResponseEntity<ErrorResponse> response =
        restTemplate.postForEntity(API_MOVIE_BATTLE + CREATE_SESSION, null, ErrorResponse.class);

    final ErrorResponse expectedError =
        ErrorResponse.builder().code("java.lang.RuntimeException").message("Some error").build();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(expectedError, response.getBody());
  }

  @Test
  public void shouldCreateSessionSuccessfully() {

    final GameSession expectedGameSession = createGameSession(0, 0, LocalDateTime.now(), null);

    when(gameSessionManagement.createNew()).thenReturn(expectedGameSession);

    ResponseEntity<GameSession> response =
        restTemplate.postForEntity(API_MOVIE_BATTLE + CREATE_SESSION, null, GameSession.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedGameSession, response.getBody());
  }

  @Test
  public void shouldThrowSessionNotFoundFromUseCase() {

    final ErrorResponse expectedError =
        ErrorResponse.builder()
            .code(NOT_FOUND.getReasonPhrase())
            .message(SESSION_NOT_FOUND)
            .build();

    when(gameSessionManagement.createNew()).thenThrow(new SessionNotFoundException());

    ResponseEntity<ErrorResponse> response =
        restTemplate.postForEntity(API_MOVIE_BATTLE + CREATE_SESSION, null, ErrorResponse.class);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(expectedError, response.getBody());
  }

  @Test
  public void shouldDeleteSessionSuccessfully() throws URISyntaxException {

    final GameSession expectedGameSession =
        createGameSession(2, 2, LocalDateTime.now(), LocalDateTime.now());

    when(gameSessionManagement.end(SESSION_ID)).thenReturn(expectedGameSession);

    final RequestEntity<GameSession> requestEntity =
        new RequestEntity<>(
            HttpMethod.DELETE, new URI(API_MOVIE_BATTLE + "/sessions/" + SESSION_ID));

    ResponseEntity<GameSession> response = restTemplate.exchange(requestEntity, GameSession.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedGameSession, response.getBody());
  }
}
