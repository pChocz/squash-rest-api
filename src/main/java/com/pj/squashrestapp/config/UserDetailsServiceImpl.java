package com.pj.squashrestapp.config;

import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private PlayerRepository playerRepository;

  @Override
  public UserDetails loadUserByUsername(final String usernameOrEmail) throws UsernameNotFoundException {
    final Player player = playerRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    if (player == null) {
      throw new UsernameNotFoundException("User not found!");
    }
    final PlayerDetails playerDetails = new PlayerDetails(player);
    return playerDetails;
  }

}
