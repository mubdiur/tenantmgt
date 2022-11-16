package io.github.tenantmgt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.tenantmgt.model.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>{
}
