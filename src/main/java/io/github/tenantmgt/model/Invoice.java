package io.github.tenantmgt.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity() @Table(name = "invoicetable") @Data @NoArgsConstructor @AllArgsConstructor
public class Invoice {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private boolean isPaid;
    @ManyToOne(fetch = FetchType.EAGER)
    private User issuedBy;
    @ManyToOne(fetch = FetchType.EAGER)
    private User issuedTo;
    private ZonedDateTime issueDate;
    private ZonedDateTime dueDate;
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<Bill> bills = new ArrayList<>();

}
