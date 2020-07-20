package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.Player;
import com.thedeanda.lorem.LoremIpsum;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@UtilityClass
public class FakePlayersCreator {

  public List<Player> create(final int numberOfAllPlayers) {

    final List<Player> players = new ArrayList<>();

    for (int i=0; i<numberOfAllPlayers; i++) {
      final String name = LoremIpsum.getInstance().getNameMale();
      final String email = name.replace(" ", "_").toLowerCase() + "@gmail.com";

      final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 12);
      final String firstNameLowercase = name.substring(0, name.indexOf(" ")).toLowerCase();
      final String passwordEncoded = bCryptPasswordEncoder.encode(firstNameLowercase);

      final Player player = new Player(name, email);
      player.setPassword(passwordEncoded);
      player.setEnabled(true);

      players.add(player);
    }

    return players;
  }

}
