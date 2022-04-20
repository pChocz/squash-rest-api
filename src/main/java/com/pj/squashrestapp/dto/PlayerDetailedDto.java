package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.pj.squashrestapp.model.Authority;
import com.pj.squashrestapp.model.AuthorityType;
import com.pj.squashrestapp.model.LeagueRole;
import com.pj.squashrestapp.model.Player;
import com.pj.squashrestapp.model.RoleForLeague;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDetailedDto {

    @EqualsAndHashCode.Include
    UUID uuid;

    Long id;
    String username;
    String emoji;
    String email;
    String locale;
    List<AuthorityType> authorities;
    List<LeagueRoleDto> leagueRoles;
    boolean nonLocked;
    boolean enabled;
    boolean wantsEmails;
    Long successfulLoginAttempts;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    LocalDateTime registrationDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    LocalDateTime lastLoggedInDateTime;

    public PlayerDetailedDto(final Player player) {
        this.id = player.getId();
        this.uuid = player.getUuid();
        this.username = player.getUsername();
        this.emoji = player.getEmoji();
        this.email = player.getEmail();
        this.locale = player.getLocale();
        this.nonLocked = player.isNonLocked();
        this.enabled = player.isEnabled();
        this.wantsEmails = player.getWantsEmails();
        this.successfulLoginAttempts = player.getSuccessfulLoginAttempts();
        this.registrationDateTime = player.getRegistrationDateTime();
        this.lastLoggedInDateTime = player.getLastLoggedInDateTime();

        this.authorities = new ArrayList<>();
        for (final Authority authority : player.getAuthorities()) {
            this.authorities.add(authority.getType());
        }

        final List<RoleForLeague> rolesSorted = player.getRoles().stream()
                .sorted(Comparator.comparing(o -> o.getLeague().getDateOfCreation()))
                .collect(Collectors.toList());

        this.leagueRoles = new ArrayList<>();
        for (final RoleForLeague role : rolesSorted) {
            final LeagueRoleDto leagueRole = new LeagueRoleDto(role);
            this.leagueRoles.add(leagueRole);
        }
    }

    public boolean isPlayerForLeague(final String leagueName) {
        return this.leagueRoles.stream()
                .filter(leagueRoleDto -> leagueRoleDto.getLeagueRole().equals(LeagueRole.PLAYER))
                .anyMatch(leagueRoleDto -> leagueRoleDto.getLeagueName().equals(leagueName));
    }

    public boolean isPlayerForLeague(final UUID leagueUuid) {
        return this.leagueRoles.stream()
                .filter(leagueRoleDto -> leagueRoleDto.getLeagueRole().equals(LeagueRole.PLAYER))
                .anyMatch(leagueRoleDto -> leagueRoleDto.getLeagueUuid().equals(leagueUuid));
    }

    public boolean hasAnyRoleForLeague(final UUID leagueUuid) {
        return this.leagueRoles.stream()
                .anyMatch(leagueRoleDto -> leagueRoleDto.getLeagueUuid().equals(leagueUuid));
    }

    @Override
    public String toString() {
        return username + " | uuid: " + uuid;
    }

    public List<LeagueRole> getLeagueRolesForLeague(final UUID leagueUuid) {
        return this.leagueRoles.stream()
                .filter(leagueRoleDto -> leagueRoleDto.getLeagueUuid().equals(leagueUuid))
                .map(LeagueRoleDto::getLeagueRole)
                .collect(Collectors.toList());
    }
}
