package com.pj.squashrestapp.config;

import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private PlayerRepository playerRepository;

  @Override
  public UserDetails loadUserByUsername(final String usernameOrEmail) throws UsernameNotFoundException {
    final Player player = playerRepository
            .fetchForAuthorizationByUsernameOrEmailUppercase(usernameOrEmail.toUpperCase())
            .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

    final UserDetailsImpl userDetailsImpl = new UserDetailsImpl(player);
    return userDetailsImpl;
  }

}
