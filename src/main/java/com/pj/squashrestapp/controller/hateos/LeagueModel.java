package com.pj.squashrestapp.controller.hateos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "league")
@Relation(collectionRelation = "leagues")
@JsonInclude(Include.NON_NULL)
public class LeagueModel extends RepresentationModel<LeagueModel> {

  private Long id;
  private String name;
  private List<SeasonModel> seasons;

}
