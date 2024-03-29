package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.model.enums.MatchFormatType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

/** */
@Slf4j
@Getter
public class LeagueDto {

    private final UUID leagueUuid;
    private final Long leagueId;
    private final String leagueName;
    private final MatchFormatType matchFormatType;
    private final String location;
    private final String time;
    private final Set<SeasonDto> seasons;
    private List<PlayerDto> owners;
    private List<PlayerDto> moderators;
    private Audit audit;

    public LeagueDto(final League league) {
        this.leagueUuid = league.getUuid();
        this.leagueId = league.getId();
        this.leagueName = league.getName();
        this.matchFormatType = league.getMatchFormatType();
        this.location = league.getLocation();
        this.time = league.getTime();
        this.audit = league.getAudit();

        this.seasons = league.getSeasons().stream().map(SeasonDto::new).collect(Collectors.toCollection(TreeSet::new));
    }

    public LeagueDto(final League league, final List<PlayerDto> owners, final List<PlayerDto> moderators) {
        this(league);
        this.owners = owners;
        this.moderators = moderators;
    }

    @Override
    public String toString() {
        return leagueName;
    }
}
