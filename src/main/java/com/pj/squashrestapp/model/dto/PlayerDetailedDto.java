package com.pj.squashrestapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 *
 */
@Value
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDetailedDto {

  @EqualsAndHashCode.Include
  Long id;
  String username;
  String email;

}
