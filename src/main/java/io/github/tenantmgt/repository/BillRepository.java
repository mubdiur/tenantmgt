package io.github.tenantmgt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tenantmgt.model.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>{
}
