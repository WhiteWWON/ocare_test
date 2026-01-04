package com.ocare.Ocare.adapter.in.web.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class DummyController {
    @GetMapping("/dummy")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("/api/test/dummy 진입 ok");
        System.out.println("/api/test/dummy 진입 ok");
        return ResponseEntity.ok("API 필터 토큰 인증 PASS!");
    }
}
