package com.pj.squashrestapp.dto.matchresultsdistribution;

import com.pj.squashrestapp.dto.LeagueDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeagueMatchResultDistribution {

    private LeagueDto league;
    private List<PlayerMatchResultDistribution> playerMatchResultDistributionList;

}
