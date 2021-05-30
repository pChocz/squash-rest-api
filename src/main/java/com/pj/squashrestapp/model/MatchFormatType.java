package com.pj.squashrestapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchFormatType {

  /**
   * Possible score:
   *
   * <pre>
   * 1:0
   * </pre>
   */
  ONE_GAME(1),

  /**
   * Possible scores:
   *
   * <pre>
   * 2:0
   * 2:1 (with tiebreak)
   * </pre>
   */
  BEST_OF_3(3),

  /**
   * Possible scores:
   *
   * <pre>
   * 3:0
   * 3:1
   * 3:2 (with tiebreak)
   * </pre>
   */
  BEST_OF_5(5);

  final int maxNumberOfSets;
}
