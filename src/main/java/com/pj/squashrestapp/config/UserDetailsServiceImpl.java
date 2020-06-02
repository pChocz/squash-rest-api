package com.pj.squashrestapp.config;

import com.pj.squashrestapp.model.dto.PlayerAuthDto;
import com.pj.squashrestapp.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private PlayerRepository playerRepository;

  @Override
  public UserDetails loadUserByUsername(final String usernameOrEmail) throws UsernameNotFoundException {
    final List<PlayerAuthDto> authDtoList = playerRepository.getAuthorizationData(usernameOrEmail);

    if (authDtoList.isEmpty()) {
      throw new UsernameNotFoundException("User not found!");
    }

    final PlayerAuthDetails playerAuthDetails = new PlayerAuthDetails(authDtoList);
    return playerAuthDetails;
  }

}
