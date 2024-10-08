package com.exampleJWT.demoJWT.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<String> getAdminDashboard() {
        // Chỉ ADMIN mới có thể truy cập vào endpoint này
        return ResponseEntity.ok("Welcome to Admin Dashboard! Only Admins can see this.");
    }
}
