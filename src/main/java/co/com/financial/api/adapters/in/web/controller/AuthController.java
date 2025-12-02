package co.com.financial.api.adapters.in.web.controller;


import co.com.financial.api.adapters.in.web.dto.auth.AuthRequest;
import co.com.financial.api.adapters.in.web.dto.auth.AuthResponse;
import co.com.financial.api.application.port.in.AuthenticationUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

    public AuthController( AuthenticationUseCase authenticationUseCase ) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login( @Valid @RequestBody AuthRequest request ) {
        AuthResponse resp = authenticationUseCase.login(request);
        return ResponseEntity.ok(resp);
    }
}
