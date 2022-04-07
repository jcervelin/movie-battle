package io.jcervelin.moviebattle.security.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "tb_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true) // anotação para não deixar o email se repetir.
  private String email;

  private String name;
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "tb_user_role",
      joinColumns =
          @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_to_role")),
      inverseJoinColumns =
          @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_role_to_user")))
  private Set<RoleEntity> roles;
}
