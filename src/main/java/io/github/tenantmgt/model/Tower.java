package io.github.tenantmgt.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity() @Table(name = "towertable") @Data @NoArgsConstructor @AllArgsConstructor
public class Tower {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<User> owners = new ArrayList<>();
	@OneToOne(fetch = FetchType.EAGER)
    private User manager;
	@OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<Floor> floors = new ArrayList<>();
    private String address;
}
