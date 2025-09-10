package org.example.remedy.global.security.auth;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.user.exception.UserNotFoundException;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthDetailsService implements UserDetailsService {
    private final UserPersistencePort userPersistencePort;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new AuthDetails(
                userPersistencePort.findByEmail(email)
                        .orElseThrow(()->UserNotFoundException.EXCEPTION)
        );
    }
}
