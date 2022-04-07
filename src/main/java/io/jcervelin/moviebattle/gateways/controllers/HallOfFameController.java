package io.jcervelin.moviebattle.gateways.controllers;

import static io.jcervelin.moviebattle.domains.EndpointConstants.API_MOVIE_BATTLE;
import static io.jcervelin.moviebattle.domains.EndpointConstants.PLAYERS;

import io.jcervelin.moviebattle.domains.HallPosition;
import io.jcervelin.moviebattle.usecases.HallOfFameGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = API_MOVIE_BATTLE)
@Api(
    value = "Hall of Fame Controller",
    tags = {"Hall of Fame"})
public class HallOfFameController {

  private final HallOfFameGenerator hallOfFameGenerator;

  @ApiOperation(value = "List Top 10 Players", response = Iterable.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success|OK"),
        @ApiResponse(code = 400, message = "Bad Request!"),
        @ApiResponse(code = 401, message = "Not Authorized!"),
        @ApiResponse(code = 403, message = "Forbidden!"),
        @ApiResponse(code = 404, message = "Not Found!")
      })
  @GetMapping(path = PLAYERS)
  public List<HallPosition> getMoviePair() {
    return hallOfFameGenerator.getTop10();
  }
}
