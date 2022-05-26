package com.pj.squashrestapp.service;

import com.pj.squashrestapp.config.exceptions.GeneralBadRequestException;
import com.pj.squashrestapp.dto.PlayerDetailedDto;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.repository.LeagueRepository;
import com.pj.squashrestapp.repository.PlayerRepository;
import com.pj.squashrestapp.repository.RoleForLeagueRepository;
import com.pj.squashrestapp.util.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeagueRolesService {

    private final PlayerRepository playerRepository;
    private final LeagueRepository leagueRepository;
    private final RoleForLeagueRepository roleForLeagueRepository;

    public boolean assignRoleByPlayerUuid(final UUID leagueUuid, final UUID playerUuid, final LeagueRole leagueRole) {
        final Player player = playerRepository.fetchForAuthorizationByUuid(playerUuid).orElseThrow();
        assignRole(leagueUuid, leagueRole, player);
        return true;
    }

    public boolean unassignRoleByPlayerUuid(final UUID leagueUuid, final UUID playerUuid, final LeagueRole leagueRole) {
        final Player player = playerRepository.fetchForAuthorizationByUuid(playerUuid).orElseThrow();
        unassignRole(leagueUuid, leagueRole, player);
        return true;
    }

    private void unassignRole(UUID leagueUuid, LeagueRole leagueRole, Player player) {
        final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
        final RoleForLeague roleForLeague = roleForLeagueRepository.findByLeagueAndLeagueRole(league, leagueRole);

        player.removeRole(roleForLeague);
        playerRepository.save(player);
        roleForLeagueRepository.save(roleForLeague);
    }

    public boolean assignRoleByPlayerUsername(
            final UUID leagueUuid, final String playerUsername, final LeagueRole leagueRole) {
        final Optional<Player> player = playerRepository.fetchForAuthorizationByUsernameOrEmailUppercase(playerUsername.toUpperCase());
        if (player.isPresent()) {
            assignRole(leagueUuid, leagueRole, player.get());
            return true;
        } else {
            return false;
        }
    }

    private void assignRole(UUID leagueUuid, LeagueRole leagueRole, Player player) {
        final League league = leagueRepository.findByUuid(leagueUuid).orElseThrow();
        final RoleForLeague roleForLeague = roleForLeagueRepository.findByLeagueAndLeagueRole(league, leagueRole);

        player.addRole(roleForLeague);
        playerRepository.save(player);
        roleForLeagueRepository.save(roleForLeague);
    }

    public boolean unassignRoleByPlayerUsername(
            final UUID leagueUuid, final String playerUsername, final LeagueRole leagueRole) {
        final Optional<Player> player = playerRepository.fetchForAuthorizationByUsernameOrEmailUppercase(playerUsername.toUpperCase());
        if (player.isPresent()) {
            unassignRole(leagueUuid, leagueRole, player.get());
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void leaveLeague(final UUID leagueUuid) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final Player player = playerRepository
                .fetchForAuthorizationByUsernameOrEmailUppercase(auth.getName().toUpperCase())
                .orElseThrow();

        final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
        final boolean hasAnyRoleForLeague = userBasicInfo.hasAnyRoleForLeague(leagueUuid);

        if (!hasAnyRoleForLeague) {
            throw new GeneralBadRequestException(ErrorCode.NOT_A_PLAYER_OF_LEAGUE);
        }

        final List<LeagueRole> leagueRoleList = userBasicInfo.getLeagueRolesForLeague(leagueUuid);

        for (final LeagueRole role : leagueRoleList) {
            unassignRoleByPlayerUuid(leagueUuid, player.getUuid(), role);
        }
    }

    public void joinLeagueAsPlayer(final UUID leagueUuid) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final Player player = playerRepository
                .fetchForAuthorizationByUsernameOrEmailUppercase(auth.getName().toUpperCase())
                .orElseThrow();

        final PlayerDetailedDto userBasicInfo = new PlayerDetailedDto(player);
        final boolean isPlayerForLeague = userBasicInfo.isPlayerForLeague(leagueUuid);

        if (isPlayerForLeague) {
            throw new GeneralBadRequestException(ErrorCode.ALREADY_A_PLAYER_OF_LEAGUE);
        }

        final UUID playerUuid = player.getUuid();
        assignRoleByPlayerUuid(leagueUuid, playerUuid, LeagueRole.PLAYER);
    }
}
