package io.so1s.backend.domain.auth.entity;

import io.so1s.backend.domain.auth.vo.UserRole;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserToRole {

  @Id
  private Long id;

  @ManyToOne
  @ToString.Exclude
  private User user;

  private UserRole userRole;

}
