package io.jcervelin.moviebattle.gateways.clients.omdpapi.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmdbMovieSummaryResponse {
  @JsonProperty("Response")
  private String response;

  @JsonProperty(value = "Search")
  private List<MovieSummary> movieSummaryList;
}
