package com.pj.squashrestapp.model;

public enum SetWinningType {

  /**
   * Examples of scores if set is played up to 11 points
   *
   * <pre>
   * 11 : 6
   * 12 : 10
   * 14 : 12
   * </pre>
   */
  ADV_OF_2_ABSOLUTE,

  /**
   * Examples of scores if set is played up to 11 points
   *
   * <pre>
   * 11 : 6
   * 11 : 9
   * 11 : 10
   * </pre>
   */
  WINNING_POINTS_ABSOLUTE,

  /**
   * Examples of scores if set is played up to 11 points
   *
   * <pre>
   * 11 : 6
   * 11 : 9
   * 12 : 10
   * 12 : 11
   * </pre>
   */
  ADV_OF_2_OR_1_AT_THE_END
}
