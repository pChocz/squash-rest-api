package com.pj.squashrestapp.dto.leaguestats;

import com.pj.squashrestapp.dto.LeagueDtoSimple;
import com.pj.squashrestapp.dto.PlayerDto;
import com.pj.squashrestapp.dto.Trophy;
import com.pj.squashrestapp.model.TrophyForLeague;
import com.pj.squashrestapp.model.audit.Audit;
import lombok.Getter;

/** */
@Getter
public class TrophyDto {

    private final Trophy trophy;
    private final PlayerDto player;
    private LeagueDtoSimple league;
    private Audit audit;

    public TrophyDto(final Trophy trophy, final PlayerDto player) {
        this.trophy = trophy;
        this.player = player;
    }

    public TrophyDto(final TrophyForLeague trophyForLeague) {
        this.trophy = trophyForLeague.getTrophy();
        this.player = new PlayerDto(trophyForLeague.getPlayer());
        this.league = new LeagueDtoSimple(trophyForLeague.getLeague());
        this.audit = trophyForLeague.getAudit();
    }
}
