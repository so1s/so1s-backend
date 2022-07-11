package io.so1s.backend.domain.auth.repository;

import io.so1s.backend.domain.auth.entity.UserToRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserToRoleRepository extends JpaRepository<UserToRole, Long> {

}
