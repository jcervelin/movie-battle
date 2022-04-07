package io.jcervelin.moviebattle.gateways.controllers;

import static io.jcervelin.moviebattle.domains.EndpointConstants.API_MOVIE_BATTLE;
import static io.jcervelin.moviebattle.domains.EndpointConstants.CREATE_SESSION;
import static io.jcervelin.moviebattle.domains.EndpointConstants.PLAYERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.jcervelin.moviebattle.configs.TestSecurityConfig;
import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.HallPosition;
import io.jcervelin.moviebattle.usecases.HallOfFameGenerator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestSecurityConfig.class)
public class HallOfFameControllerTest {

  @MockBean private HallOfFameGenerator hallOfFameGenerator;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  public void shouldReturnSuccessSessionIdIsValid() {

    final HallPosition hallPosition1 =
        HallPosition.builder().absoluteScore(3).username("User1").build();

    final HallPosition hallPosition2 =
        HallPosition.builder().absoluteScore(2).username("User2").build();

    final List<HallPosition> positions = List.of(hallPosition1, hallPosition2);

    when(hallOfFameGenerator.getTop10()).thenReturn(positions);

    // Create session id
    ResponseEntity<GameSession> sessionResponse =
        restTemplate.postForEntity(API_MOVIE_BATTLE + CREATE_SESSION, null, GameSession.class);
    final String sessionId = sessionResponse.getBody().getSessionId();

    // Send created session id to pass through @ValidateSession
    ResponseEntity<List<HallPosition>> response =
        restTemplate.exchange(
            API_MOVIE_BATTLE + PLAYERS,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertThat(response.getBody()).isEqualTo(positions);
    verify(hallOfFameGenerator).getTop10();
  }
}
