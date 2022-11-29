package io.github.tenantmgt.service;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.tenantmgt.model.Apartment;
import io.github.tenantmgt.model.Floor;
import io.github.tenantmgt.model.Invoice;
import io.github.tenantmgt.model.Role;
import io.github.tenantmgt.model.Tower;
import io.github.tenantmgt.model.User;
import io.github.tenantmgt.repository.ApartmentRepository;
import io.github.tenantmgt.repository.FloorRepository;
import io.github.tenantmgt.repository.InvoiceRepository;
import io.github.tenantmgt.repository.RoleRepository;
import io.github.tenantmgt.repository.TowerRepository;
import io.github.tenantmgt.repository.UserRepository;
import io.github.tenantmgt.serviceinterface.UserServiceInterface;
import lombok.RequiredArgsConstructor;
@Service @RequiredArgsConstructor @Transactional
public class UserService implements UserServiceInterface, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ApartmentRepository apartmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final FloorRepository floorRepository;
    private final TowerRepository towerRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username was not found");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
            );
    }

    @Override
    public Apartment getTenantApartment(String username) {
        User user = getUser(username);
        Apartment apartment = apartmentRepository.findByTenant(user);
        return apartment;
    }

    @Override
    public Collection<Invoice> getTenantInvoices(String username) {
        User user = getUser(username);
        return invoiceRepository.getTenantInvoices(user.getId());
    }

    @Override
    @Query("SELECT ")
    public Floor getTenantFloor(String username) {
        Apartment apartment = getTenantApartment(username);
        Long floorId = floorRepository.getTenantFloorID(apartment.getId());
        Floor floor = floorRepository.findById(floorId).get();
        return floor;
    }

    @Override
    public Tower getTenantTower(String username) {
        Floor floor = getTenantFloor(username);
        Long towerId = towerRepository.getTenantTowerID(floor.getId());
        Tower tower = towerRepository.findById(towerId).get();
        return tower;
    }

}
