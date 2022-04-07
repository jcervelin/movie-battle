package io.jcervelin.moviebattle.gateways.controllers;

import static io.jcervelin.moviebattle.domains.EndpointConstants.ANSWER;
import static io.jcervelin.moviebattle.domains.EndpointConstants.API_MOVIE_BATTLE;
import static io.jcervelin.moviebattle.domains.EndpointConstants.CREATE_SESSION;
import static io.jcervelin.moviebattle.domains.EndpointConstants.QUESTION;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.jcervelin.moviebattle.configs.TestSecurityConfig;
import io.jcervelin.moviebattle.domains.AnswerRequest;
import io.jcervelin.moviebattle.domains.AnswerResponse;
import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.MovieChoice;
import io.jcervelin.moviebattle.domains.QuestionResponse;
import io.jcervelin.moviebattle.usecases.AnswerChecker;
import io.jcervelin.moviebattle.usecases.QuestionGenerator;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestSecurityConfig.class)
public class MovieBattleControllerTest {

  @Autowired private TestRestTemplate restTemplate;

  @MockBean private QuestionGenerator questionGenerator;

  @MockBean private AnswerChecker answerChecker;

  @Test
  public void shouldThrowSessionValidationException() throws JSONException {
    String questionRequestJson = "{\"sessionId\":\"sessionId123\"}";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(questionRequestJson, headers);

    // send json with POST
    ResponseEntity<String> response =
        restTemplate.postForEntity(API_MOVIE_BATTLE + QUESTION, entity, String.class);

    String expectedJson =
        "{\"code\":\"Bad Request\",\"message\":\"Field name: [ sessionId ] "
            + "- Message: [ Invalid session id: sessionId123 ]\"}";
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    JSONAssert.assertEquals(expectedJson, response.getBody(), false);
  }

  @Test
  public void shouldReturnSuccessSessionIdIsValid() {
    final QuestionResponse expectedResponse =
        QuestionResponse.builder()
            .choice1(MovieChoice.builder().id("id1").name("Movie 1").build())
            .choice2(MovieChoice.builder().id("id2").name("Movie 2").build())
            .build();

    // Create session id
    ResponseEntity<GameSession> sessionResponse =
        restTemplate.postForEntity(API_MOVIE_BATTLE + CREATE_SESSION, null, GameSession.class);
    final String sessionId = sessionResponse.getBody().getSessionId();

    when(questionGenerator.getMoviePair(sessionId)).thenReturn(expectedResponse);

    // create request with session id
    String questionRequestJson = format("{\"sessionId\":\"%s\"}", sessionId);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(questionRequestJson, headers);

    // Send created session id to pass through @ValidateSession
    ResponseEntity<QuestionResponse> response =
        restTemplate.postForEntity(API_MOVIE_BATTLE + QUESTION, entity, QuestionResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertThat(response.getBody()).isEqualTo(expectedResponse);
    verify(questionGenerator).getMoviePair(sessionId);
  }

  @Test
  public void shouldReturnAnswerSuccessfully() {

    // Create session id
    ResponseEntity<GameSession> sessionResponse =
        restTemplate.postForEntity(API_MOVIE_BATTLE + CREATE_SESSION, null, GameSession.class);
    final String sessionId = sessionResponse.getBody().getSessionId();

    // create request with session id
    final AnswerRequest answerRequest =
        AnswerRequest.builder().sessionId(sessionId).answer("movieId123").build();

    final AnswerResponse expectedResponse =
        AnswerResponse.builder().sessionId(sessionId).score(2).endGame(false).build();

    when(answerChecker.checkAnswer(answerRequest)).thenReturn(expectedResponse);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<AnswerRequest> entity = new HttpEntity<>(answerRequest, headers);

    // Send created session id to pass through @ValidateSession
    ResponseEntity<AnswerResponse> response =
        restTemplate.postForEntity(API_MOVIE_BATTLE + ANSWER, entity, AnswerResponse.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertThat(response.getBody()).isEqualTo(expectedResponse);
    verify(answerChecker).checkAnswer(answerRequest);
  }
}
