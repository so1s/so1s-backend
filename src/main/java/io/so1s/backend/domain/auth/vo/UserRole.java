package io.so1s.backend.domain.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

// 상위 역할의 사용자는 하위 권한의 사용자 계정을 생성할 수 있고,
// 하위 역할이 할 수 있는 일을 수행할 수 있다.
@ToString
@AllArgsConstructor
@Getter
public enum UserRole {

  // 앱을 실행할 때 최초로 계정이 발급되는 가장 높은 권한
  OWNER(1),
  // API 서버의 관리자 역할
  ADMIN(2),
  // 인퍼런스 서버의 사용자 역할
  USER(3);

  final int id;

}
