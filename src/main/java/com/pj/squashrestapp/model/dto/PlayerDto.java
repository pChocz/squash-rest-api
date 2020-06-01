package com.pj.squashrestapp.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 *
 */
@Value
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDto {

  @EqualsAndHashCode.Include
  Long id;
  String username;

}
