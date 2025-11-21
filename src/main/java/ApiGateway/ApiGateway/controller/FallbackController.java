package ApiGateway.ApiGateway.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/products")
    public ResponseEntity<String> productFallback() {
        return ResponseEntity.ok("⚠️ Product Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/users")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.ok("⚠️ User Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/auth")
    public ResponseEntity<String> authFallback() {
        return ResponseEntity.ok("⚠️ Authentication Service is currently unavailable. Please try again later.");
    }
}
