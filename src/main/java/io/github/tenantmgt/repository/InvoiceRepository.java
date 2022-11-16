package io.github.tenantmgt.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.tenantmgt.model.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query("SELECT * FROM invoicetable t WHERE t.issued_to_id = ?1")
    public Collection<Invoice> getTenantInvoices(Long userid);
}
