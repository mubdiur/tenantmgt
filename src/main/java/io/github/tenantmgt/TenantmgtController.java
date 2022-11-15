package io.github.tenantmgt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.ResourceResolver;

@RestController
public class TenantmgtController {
    @GetMapping("/login")
    public String login() {
        return "hello from login";
    }
}
