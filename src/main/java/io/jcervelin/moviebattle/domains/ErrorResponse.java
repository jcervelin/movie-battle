package io.jcervelin.moviebattle.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class ErrorResponse {
  private String code;
  private String message;

  public static ErrorResponse of(String message) {
    return ErrorResponse.builder().message(message).build();
  }

  public static ErrorResponse of(String message, String code) {
    return ErrorResponse.builder().message(message).code(code).build();
  }
}
