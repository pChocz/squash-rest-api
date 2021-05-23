package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.Player;
import com.thedeanda.lorem.LoremIpsum;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

/** */
@UtilityClass
public class FakePlayersCreator {

  public List<Player> create(final int numberOfAllPlayers, final PasswordEncoder passwordEncoder) {
    final List<Player> players = new ArrayList<>();

    for (int i = 0; i < numberOfAllPlayers; i++) {
      final String name = LoremIpsum.getInstance().getNameMale();
      final String email = name.replace(" ", "_").toLowerCase() + "@gmail.com";
      final String passwordEncoded = encodePassword(name, passwordEncoder);

      final Player player = new Player(name, email);
      player.setPassword(passwordEncoded);
      player.setEnabled(true);

      players.add(player);
    }

    return players;
  }

  private String encodePassword(final String name, final PasswordEncoder passwordEncoder) {
    final String firstNameLowercase = name.substring(0, name.indexOf(" ")).toLowerCase();
    return passwordEncoder.encode(firstNameLowercase);
  }
}
