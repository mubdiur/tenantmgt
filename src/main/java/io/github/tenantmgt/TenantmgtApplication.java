package io.github.tenantmgt;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.github.tenantmgt.model.Role;
import io.github.tenantmgt.model.User;
import io.github.tenantmgt.service.UserService;

@SpringBootApplication
public class TenantmgtApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenantmgtApplication.class, args);
	}



	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_TENANT"));
			userService.saveRole(new Role(null, "ROLE_OWNER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));

			userService.saveUser(
					new User(
							null,
							"Md. Mubdiur Rahman",
							"AdminUser",
							"1234",
							"mubdiur@gmail.com",
							"+8801232837298",
							"38947398473898",
							"387483748",
							new ArrayList<>()));

			userService.addRoleToUser("AdminUser", "ROLE_USER");
			userService.addRoleToUser("AdminUser", "ROLE_ADMIN");

			userService.saveUser(
					new User(
							null,
							"Md. Mubdiur Rahman",
							"NormalUser",
							"1234",
							"mubdiur@gmail.com",
							"+8801232837298",
							"38947398473898",
							"387483748",
							new ArrayList<>()));

			userService.addRoleToUser("NormalUser", "ROLE_USER");

			userService.saveUser(
					new User(
							null,
							"Md. Mubdiur Rahman",
							"TenantUser",
							"1234",
							"mubdiur@gmail.com",
							"+8801232837298",
							"38947398473898",
							"387483748",
							new ArrayList<>()));

			userService.addRoleToUser("TenantUser", "ROLE_USER");
			userService.addRoleToUser("TenantUser", "ROLE_TENANT");

			userService.saveUser(
					new User(
							null,
							"Md. Mubdiur Rahman",
							"ManagerUser",
							"1234",
							"mubdiur@gmail.com",
							"+8801232837298",
							"38947398473898",
							"387483748",
							new ArrayList<>()));

			userService.addRoleToUser("ManagerUser", "ROLE_USER");
			userService.addRoleToUser("ManagerUser", "ROLE_MANAGER");

			userService.saveUser(
					new User(
							null,
							"Md. Mubdiur Rahman",
							"OwnerUser",
							"1234",
							"mubdiur@gmail.com",
							"+8801232837298",
							"38947398473898",
							"387483748",
							new ArrayList<>()));

			userService.addRoleToUser("OwnerUser", "ROLE_USER");
			userService.addRoleToUser("OwnerUser", "ROLE_OWNER");
		};
	}
}
