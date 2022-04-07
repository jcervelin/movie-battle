package io.jcervelin.moviebattle.gateways.controllers;

import static io.jcervelin.moviebattle.domains.EndpointConstants.API_MOVIE_BATTLE;
import static io.jcervelin.moviebattle.domains.EndpointConstants.CREATE_SESSION;
import static io.jcervelin.moviebattle.domains.EndpointConstants.DELETE_SESSION;

import io.jcervelin.moviebattle.domains.GameSession;
import io.jcervelin.moviebattle.domains.QuestionResponse;
import io.jcervelin.moviebattle.usecases.GameSessionManagement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = API_MOVIE_BATTLE)
@Api(
    value = "Start new Game",
    tags = {"Where you start a new Game"})
@Slf4j
public class SessionController {

  private final GameSessionManagement gameSessionManagement;

  @ApiOperation(value = "Start a new Game ", response = QuestionResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success|OK"),
        @ApiResponse(code = 400, message = "Bad Request!"),
        @ApiResponse(code = 401, message = "Not Authorized!"),
        @ApiResponse(code = 403, message = "Forbidden!"),
        @ApiResponse(code = 404, message = "Not Found!")
      })
  @PostMapping(path = CREATE_SESSION)
  public GameSession createSession() {
    return gameSessionManagement.createNew();
  }

  @ApiOperation(value = "End Game ", response = QuestionResponse.class)
  @DeleteMapping(path = DELETE_SESSION)
  public GameSession endSession(@PathVariable String sessionId) {
    log.info("Session to be ended: {}", sessionId);
    return gameSessionManagement.end(sessionId);
  }
}
