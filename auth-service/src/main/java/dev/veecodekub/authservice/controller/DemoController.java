package dev.veecodekub.authservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')") // user12 มี ROLE_USER น่าจะเข้าได้
    public String userEndpoint() {
        return "Hello User! You verified the token successfully.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // user12 มี ROLE_ADMIN น่าจะเข้าได้
    public String adminEndpoint() {
        return "Hello Admin! You have high privileges.";
    }

    @GetMapping("/me")
    public String whoAmI(Authentication auth) {
        // เช็คว่า Spring เห็นเราเป็นใคร และมี Role อะไรบ้าง
        return "User: " + auth.getName() + " | Roles: " + auth.getAuthorities();
    }
}
