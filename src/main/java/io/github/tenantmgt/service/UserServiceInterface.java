package io.github.tenantmgt.service;

import org.springframework.stereotype.Service;

import io.github.tenantmgt.model.Role;
import io.github.tenantmgt.model.User;

public interface UserServiceInterface {
    User saveUser(User user);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    User getUser(String username);
}
