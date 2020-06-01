package com.pj.squashrestapp.model.projection;

import com.pj.squashrestapp.model.Match;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(name = "matchProjection", types = {Match.class})
@SuppressWarnings({"JavaDoc", "unused"})
public interface MatchProjection {

  @Value("#{target.id}")
  long getId();

  @Value("#{target.firstPlayer.name}")
  String getFirstPlayerName();

  @Value("#{target.secondPlayer.name}")
  String getSecondPlayerName();

  @Value("#{target.roundGroup.number}")
  long getRoundGroupNumber();

  @Value("#{target.roundGroup.round.number}")
  long getRoundNumber();

  @Value("#{target.roundGroup.round.season.number}")
  long getSeasonNumber();

  List<SetResultProjection> getSetResults();

}
