package io.github.tenantmgt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.tenantmgt.model.Floor;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    @Query("SELECT floor_id FROM floortable_apartments t WHERE t.apartments_id = ?1")
    public Long getTenantFloorID(Long apartmentId);
}
