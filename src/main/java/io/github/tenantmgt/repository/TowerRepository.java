package io.github.tenantmgt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.tenantmgt.model.Tower;

@Repository
public interface TowerRepository extends JpaRepository<Tower, Long> {
    @Query("SELECT tower_id FROM towertable_floors t WHERE t.floors_id = ?1")
    public Long getTenantTowerID(Long floorId);
}
