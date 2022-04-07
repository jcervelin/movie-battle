package io.jcervelin.moviebattle.usecases;

import io.jcervelin.moviebattle.domains.entities.MovieEntity;
import io.jcervelin.moviebattle.gateways.clients.omdpapi.OmdbApi;
import io.jcervelin.moviebattle.gateways.clients.omdpapi.domains.OmdbMovieDetailsResponse;
import io.jcervelin.moviebattle.gateways.databases.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieLoader {

  private static final String TRUE = "True";

  private final OmdbApi omdbApi;
  private final MovieRepository movieRepository;

  @Value("${clients.api-key}")
  private String apiKey;

  public void loadMovies(List<String> keywords) {

    final List<CompletableFuture<OmdbMovieDetailsResponse>> futures = new ArrayList<>();

    keywords.forEach(keyword -> getMovies(futures, keyword));

    final List<OmdbMovieDetailsResponse> movieDetails =
        futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

    log.debug("Movies loaded from Omdb Api: {}", movieDetails);

    final List<MovieEntity> omdbEntities =
        movieDetails.stream()
            .map(
                detail ->
                    MovieEntity.builder()
                        .id(detail.getImdbId())
                        .imdbRating(detail.getImdbRatingAsNumber())
                        .imdbVotes(detail.getImdbVotesAsNumber())
                        .title(detail.getName())
                        .build())
            .collect(Collectors.toList());

    movieRepository.saveAll(omdbEntities);
  }

  private void getMovies(
      List<CompletableFuture<OmdbMovieDetailsResponse>> futures, String keyword) {
    boolean hasMovies = true;
    int pages = 1;

    while (hasMovies) {
      var omdbMovieSummaryResponse =
          omdbApi.searchMovie(apiKey, keyword, pages);

      if (TRUE.equals(omdbMovieSummaryResponse.getResponse())) {
        var completableFutures =
            omdbMovieSummaryResponse.getMovieSummaryList().stream()
                .map(movieSummary -> getMovies(movieSummary.getImdbId()))
                .collect(Collectors.toList());
        futures.addAll(completableFutures);

        pages++;
        if (pages > 20) {
          hasMovies = false;
        }
      } else {
        hasMovies = false;
      }
    }
  }

  private CompletableFuture<OmdbMovieDetailsResponse> getMovies(String movieId) {
    return CompletableFuture.supplyAsync(() -> omdbApi.getMovie(apiKey, movieId));
  }
}
