package io.github.tenantmgt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tenantmgt.model.Tenant;
import io.github.tenantmgt.model.User;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Tenant findByUser(User user);
}
