package com.pj.squashrestapp.controller.hateos;

import com.pj.squashrestapp.controller.LeagueController;
import com.pj.squashrestapp.model.League;
import com.pj.squashrestapp.model.Season;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 *
 */
@Component
public class LeagueAssembler extends RepresentationModelAssemblerSupport<League, LeagueModel> {

  public LeagueAssembler() {
    super(LeagueController.class, LeagueModel.class);
  }

  @Override
  public LeagueModel toModel(final League leagueEntity) {
    final LeagueModel leagueModel = instantiateModel(leagueEntity);

//    leagueModel.add(linkTo(
//            methodOn(LeagueController.class)
//                    .overalScoreboard(leagueEntity.getId()))
//            .withRel("overal-scoreboard"));

    leagueModel.setId(leagueEntity.getId());
    leagueModel.setName(leagueEntity.getName());
    leagueModel.setSeasons(toSeasonsModel(leagueEntity.getSeasons()));

    return leagueModel;
  }

  private List<SeasonModel> toSeasonsModel(final Set<Season> seasons) {
    return seasons.isEmpty()
            ? Collections.emptyList()
            : seasons
            .stream()
            .map(season -> SeasonModel.builder()
                    .id(season.getId())
                    .number(season.getNumber())
                    .startDate(season.getStartDate())
                    .build()
//                    .add(linkTo(
//                            methodOn(SeasonController.class)
//                                    .overalScoreboard(season.getId()))
//                            .withSelfRel())
            )
            .sorted(Comparator.comparingInt(SeasonModel::getNumber))
            .collect(Collectors.toList());
  }

}
