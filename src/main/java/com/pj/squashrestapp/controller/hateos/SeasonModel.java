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

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "season")
@Relation(collectionRelation = "seasons")
@JsonInclude(Include.NON_NULL)
public class SeasonModel extends RepresentationModel<SeasonModel> {

  private Long id;
  private int number;
  private LocalDate startDate;
  private LeagueModel league;

}
