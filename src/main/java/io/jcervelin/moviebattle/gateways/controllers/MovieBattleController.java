package io.jcervelin.moviebattle.gateways.controllers;

import io.jcervelin.moviebattle.domains.AnswerRequest;
import io.jcervelin.moviebattle.domains.AnswerResponse;
import io.jcervelin.moviebattle.domains.QuestionRequest;
import io.jcervelin.moviebattle.domains.QuestionResponse;
import io.jcervelin.moviebattle.usecases.AnswerChecker;
import io.jcervelin.moviebattle.usecases.QuestionGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static io.jcervelin.moviebattle.domains.EndpointConstants.ANSWER;
import static io.jcervelin.moviebattle.domains.EndpointConstants.API_MOVIE_BATTLE;
import static io.jcervelin.moviebattle.domains.EndpointConstants.QUESTION;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = API_MOVIE_BATTLE)
@Api(
    value = "Movie Battle Rest Controller",
    tags = {"Where you play Movie Battle"})
@Slf4j
public class MovieBattleController {

  private final QuestionGenerator questionGenerator;
  private final AnswerChecker answerChecker;

  @ApiOperation(value = "Get a new question", response = QuestionResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success|OK"),
        @ApiResponse(code = 400, message = "Bad Request!"),
        @ApiResponse(code = 401, message = "Not Authorized!"),
        @ApiResponse(code = 403, message = "Forbidden!"),
        @ApiResponse(code = 404, message = "Not Found!")
      })
  @PostMapping(path = QUESTION)
  public QuestionResponse getMoviePair(@RequestBody @Valid QuestionRequest request) {
    log.info("QuestionRequest {}", request);
    return questionGenerator.getMoviePair(request.getSessionId());
  }

  @ApiOperation(
      value = "Send answer to which movie has a higher score",
      response = AnswerResponse.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success|OK"),
        @ApiResponse(code = 400, message = "Bad Request!"),
        @ApiResponse(code = 401, message = "Not Authorized!"),
        @ApiResponse(code = 403, message = "Forbidden!"),
        @ApiResponse(code = 404, message = "Not Found!")
      })
  @PostMapping(path = ANSWER)
  public AnswerResponse sendAnswer(@RequestBody @Valid AnswerRequest request) {
    log.info("AnswerRequest {}", request);
    return answerChecker.checkAnswer(request);
  }
}
