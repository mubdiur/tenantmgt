package io.github.tenantmgt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tenantmgt.model.Floor;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long>{
}
