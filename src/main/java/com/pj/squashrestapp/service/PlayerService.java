package com.pj.squashrestapp.service;

import com.pj.squashrestapp.controller.WrongSignupDataException;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.repository.AuthorityRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.util.PasswordStrengthValidator;
import com.pj.squashrestapp.util.UsernameValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class PlayerService {

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private AuthorityRepository authorityRepository;

  @SuppressWarnings("OverlyComplexMethod")
  public boolean isValidSignupData(final String username, final String email, final String password) throws WrongSignupDataException {
    final List<Player> allPlayers = playerRepository.findAll();
    final Set<String> allUsernames = allPlayers.stream().map(Player::getUsername).collect(Collectors.toSet());
    final Set<String> allEmails = allPlayers.stream().map(Player::getEmail).collect(Collectors.toSet());

    final boolean usernameTaken = allUsernames.contains(username);
    final boolean emailTaken = allEmails.contains(email);

    final String message;

    if (usernameTaken && emailTaken) {
      message = "Both username and email are already taken, maybe you should log in instead?";

    } else if (emailTaken) {
      message = "Email is already taken!";

    } else if (usernameTaken) {
      message = "Username is already taken!";

    } else if (!UsernameValidator.isValid(username)) {
      message = "Username is not valid, it must contain 5-20 characters. Allowed characters are letters, numbers, dashes, underscores and spaces";

    } else if (!EmailValidator.getInstance().isValid(email)) {
      message = "Email is not valid!";

    } else if (!PasswordStrengthValidator.isValid(password)) {
      message = "Password is too weak. It must contain at least 5 characters, at least one upper case letter and at least one lower case letter. Whitespace characters are not allowed";

    } else {
      message = "";
    }

    if (message.isEmpty()) {
      return true;

    } else {
      throw new WrongSignupDataException(message);
    }
  }

  public Player registerNewUser(final String username, final String email, final String password) {
    final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    final String hashedPassword = bCryptPasswordEncoder.encode(password);
    final Authority userAuthority = authorityRepository.findByType(AuthorityType.ROLE_USER);

    final Player player = new Player();
    player.setUsername(username);
    player.setEmail(email);
    player.setPassword(hashedPassword);
    player.addAuthority(userAuthority);
    playerRepository.save(player);

    return player;
  }

}
