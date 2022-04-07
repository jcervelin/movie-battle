package io.jcervelin.moviebattle.gateways.clients.omdpapi.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSummary {
  @JsonProperty("imdbID")
  private String imdbId;
}
