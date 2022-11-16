package io.github.tenantmgt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tenantmgt.model.Tower;

@Repository
public interface TowerRepository extends JpaRepository<Tower, Long>{
}
