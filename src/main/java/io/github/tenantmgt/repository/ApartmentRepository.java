package io.github.tenantmgt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tenantmgt.model.Apartment;
import io.github.tenantmgt.model.User;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long>{
    Apartment findByTenant(User tenant);
}
