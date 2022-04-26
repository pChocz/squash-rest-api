package com.pj.squashrestapp.dbinit.fake;

import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import lombok.experimental.UtilityClass;

import java.util.List;

/** */
@UtilityClass
public class FakePlayersRoleAssigner {

    public List<Player> assignRolesAndAuthorities(
            final List<Player> players,
            final RoleForLeague ownerRole,
            final RoleForLeague moderatorRole,
            final RoleForLeague playerRole,
            final Authority userAuthority) {

        for (int i = 0; i < players.size(); i++) {
            final Player player = players.get(i);
            if (i < 1) {
                player.addRole(ownerRole);
            }
            if (i < 2) {
                player.addRole(moderatorRole);
            }
            player.addRole(playerRole);
            player.addAuthority(userAuthority);
        }

        return players;
    }
}
