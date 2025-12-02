package co.com.financial.api.adapters.in.security;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class InMemoryUserDetailsService implements UserDetailsService {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return User.withUsername("admin")
                    .password(encoder.encode("admin"))
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();
        }
        if ("user".equals(username)) {
            return User.withUsername("user")
                    .password(encoder.encode("password"))
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .build();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
