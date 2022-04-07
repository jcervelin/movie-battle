package io.jcervelin.moviebattle.gateways.clients.omdpapi;

import io.jcervelin.moviebattle.gateways.clients.omdpapi.domains.OmdbMovieDetailsResponse;
import io.jcervelin.moviebattle.gateways.clients.omdpapi.domains.OmdbMovieSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${clients.movie-api}", url = "${clients.movie-api}")
public interface OmdbApi {

  @GetMapping
  OmdbMovieSummaryResponse searchMovie(
      @RequestParam("apikey") String apikey,
      @RequestParam("s") String search,
      @RequestParam("page") Integer page);

  @GetMapping
  OmdbMovieDetailsResponse getMovie(
      @RequestParam("apikey") String apikey, @RequestParam("i") String imdbId);
}
