package io.jcervelin.moviebattle.gateways.clients.omdpapi.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmdbMovieDetailsResponse {
  private static final String N_A = "N/A";

  @JsonProperty("imdbID")
  private String imdbId;

  @JsonProperty("Title")
  private String name;

  private String imdbRating;
  private String imdbVotes;

  public BigDecimal getImdbVotesAsNumber() {
    if (StringUtils.isEmpty(imdbVotes) || N_A.equals(imdbVotes)) {
      return BigDecimal.ZERO;
    }
    return new BigDecimal(imdbVotes.replaceAll(",", ""));
  }

  public BigDecimal getImdbRatingAsNumber() {
    if (StringUtils.isEmpty(imdbRating) || N_A.equals(imdbRating)) {
      return BigDecimal.ZERO;
    }
    return new BigDecimal(imdbRating);
  }
}
