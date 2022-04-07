package io.jcervelin.moviebattle.gateways.controllers;

import static io.jcervelin.moviebattle.domains.EndpointConstants.API_LOAD_MOVIES;

import io.jcervelin.moviebattle.usecases.MovieLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = API_LOAD_MOVIES)
@RequiredArgsConstructor
@Api(
    value = "Movie Loader - there are 198 preloaded movies (Api key requered for new movies)",
    tags = {"Where you load movies"})
public class MovieLoaderController {

  private final MovieLoader movieLoader;

  @ApiOperation(value = "Load movies by keywords")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Success|OK"),
        @ApiResponse(code = 400, message = "Bad Request!"),
        @ApiResponse(code = 401, message = "Not Authorized!"),
        @ApiResponse(code = 403, message = "Forbidden!"),
        @ApiResponse(code = 404, message = "Not Found!")
      })
  @PostMapping
  public ResponseEntity<Void> loadMovies(@RequestParam List<String> keywords) {
    movieLoader.loadMovies(keywords);
    return ResponseEntity.created(null).build();
  }
}
