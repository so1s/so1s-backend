package io.so1s.backend.domain.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "user_table")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class User {

  @JsonIgnore
  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(name = "username", length = 50, unique = true)
  private String username;

  @JsonIgnore
  @ToString.Exclude
  @Column(name = "password", length = 100)
  private String password;

  @Fetch(FetchMode.JOIN)
  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
  @Builder.Default
  private List<UserToRole> userToRoles = new ArrayList<>();

  public void addAllUserToRole(List<UserToRole> userToRoles) {
    this.userToRoles.addAll(userToRoles);
  }

}
