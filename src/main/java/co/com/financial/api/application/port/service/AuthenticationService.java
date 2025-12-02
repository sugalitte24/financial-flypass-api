package co.com.financial.api.application.port.service;

import co.com.financial.api.adapters.in.web.dto.auth.AuthRequest;
import co.com.financial.api.adapters.in.web.dto.auth.AuthResponse;
import co.com.financial.api.adapters.out.security.JwtUtils;
import co.com.financial.api.application.port.in.AuthenticationUseCase;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService implements AuthenticationUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthenticationService( AuthenticationManager authenticationManager, JwtUtils jwtUtils ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login( AuthRequest request ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String username = authentication.getName();
        String token = jwtUtils.generateToken(username);

        return new AuthResponse(token);
    }
}

