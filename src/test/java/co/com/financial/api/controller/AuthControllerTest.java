package co.com.financial.api.controller;


import co.com.financial.api.adapters.in.web.controller.AuthController;
import co.com.financial.api.adapters.in.web.dto.auth.AuthRequest;
import co.com.financial.api.adapters.in.web.dto.auth.AuthResponse;
import co.com.financial.api.application.port.in.AuthenticationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationUseCase authenticationUseCase;

    @InjectMocks
    private AuthController sut;

    @Test
    void login_success_returnsOkWithToken() {
        AuthRequest req = new AuthRequest("user1", "secret");
        AuthResponse resp = new AuthResponse("token-123");
        when(authenticationUseCase.login(req)).thenReturn(resp);

        ResponseEntity<AuthResponse> response = sut.login(req);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("token-123", response.getBody().token());
        verify(authenticationUseCase).login(req);
    }

    @Test
    void login_whenUseCaseThrows_propagatesException() {
        AuthRequest req = new AuthRequest("user1", "wrong");
        when(authenticationUseCase.login(req)).thenThrow(new RuntimeException("auth failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> sut.login(req));
        assertEquals("auth failed", ex.getMessage());
        verify(authenticationUseCase).login(req);
    }
}

