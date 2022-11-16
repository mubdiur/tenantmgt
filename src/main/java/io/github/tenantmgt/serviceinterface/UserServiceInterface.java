package io.github.tenantmgt.serviceinterface;


import java.util.Collection;
import io.github.tenantmgt.model.Apartment;
import io.github.tenantmgt.model.Floor;
import io.github.tenantmgt.model.Invoice;
import io.github.tenantmgt.model.Role;
import io.github.tenantmgt.model.Tower;
import io.github.tenantmgt.model.User;

public interface UserServiceInterface {
    User saveUser(User user);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    User getUser(String username);

    Apartment getTenantApartment(String username);

    Collection<Invoice> getTenantInvoices(String username);
    
    Floor getTenantFloor(String username);

    Tower getTenantTower(String username);
    
}
