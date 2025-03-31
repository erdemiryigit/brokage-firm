package com.erdemiryigit.brokagefirm.repository;

import com.erdemiryigit.brokagefirm.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(Role.RoleName roleName);
}
