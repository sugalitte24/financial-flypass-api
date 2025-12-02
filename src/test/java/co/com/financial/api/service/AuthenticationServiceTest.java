package co.com.financial.api.service;


import co.com.financial.api.adapters.in.web.dto.auth.AuthRequest;
import co.com.financial.api.adapters.in.web.dto.auth.AuthResponse;
import co.com.financial.api.adapters.out.security.JwtUtils;
import co.com.financial.api.application.port.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthenticationService sut;

    @Test
    void login_success_returnsToken() {
        AuthRequest req = new AuthRequest("user1", "secret");
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getName()).thenReturn("user1");
        when(jwtUtils.generateToken("user1")).thenReturn("token-abc");

        AuthResponse resp = sut.login(req);

        assertNotNull(resp);
        assertEquals("token-abc", resp.token());
        verify(authenticationManager).authenticate(any());
        verify(jwtUtils).generateToken("user1");
    }

    @Test
    void login_whenAuthFails_propagatesException() {
        AuthRequest req = new AuthRequest("user1", "wrong");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThrows(BadCredentialsException.class, () -> sut.login(req));
        verify(authenticationManager).authenticate(any());
        verifyNoInteractions(jwtUtils);
    }
}
