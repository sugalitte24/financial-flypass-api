package co.com.financial.api.application.port.in;

import co.com.financial.api.adapters.in.web.dto.auth.AuthRequest;
import co.com.financial.api.adapters.in.web.dto.auth.AuthResponse;

public interface AuthenticationUseCase {
    AuthResponse login( AuthRequest request );
}
