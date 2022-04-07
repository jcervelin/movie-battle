package io.jcervelin.moviebattle.security.domains;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role implements Serializable {

  private Long id;
  private String roleName;
}
