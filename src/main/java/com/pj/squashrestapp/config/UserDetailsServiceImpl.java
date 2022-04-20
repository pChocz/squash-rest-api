package com.pj.squashrestapp.config;

import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PlayerRepository playerRepository;

    @Override
    public UserDetails loadUserByUsername(final String usernameOrEmail) throws UsernameNotFoundException {
        final Player player = playerRepository
                .fetchForAuthorizationByUsernameOrEmailUppercase(usernameOrEmail.toUpperCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        final UserDetailsImpl userDetailsImpl = new UserDetailsImpl(player);
        return userDetailsImpl;
    }
}
