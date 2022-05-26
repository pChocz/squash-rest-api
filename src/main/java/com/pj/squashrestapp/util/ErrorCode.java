package com.pj.squashrestapp.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCode {

    // NOT FOUND
    public static final String LEAGUE_NOT_FOUND = "LEAGUE_NOT_FOUND";
    public static final String SEASON_NOT_FOUND = "SEASON_NOT_FOUND";
    public static final String ROUND_NOT_FOUND = "ROUND_NOT_FOUND";
    public static final String MATCH_NOT_FOUND = "MATCH_NOT_FOUND";
    public static final String LOGO_NOT_FOUND = "LOGO_NOT_FOUND";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";

    // INVALID
    public static final String WRONG_DATA_FORMAT = "WRONG_DATA_FORMAT";
    public static final String INVALID_PASSWORD_RESET_TOKEN = "INVALID_PASSWORD_RESET_TOKEN";
    public static final String INVALID_REFRESH_TOKEN = "INVALID_REFRESH_TOKEN";
    public static final String INVALID_MAGIC_LINK_TOKEN = "INVALID_MAGIC_LINK_TOKEN";
    public static final String INVALID_ACCOUNT_ACTIVATION_TOKEN = "INVALID_ACCOUNT_ACTIVATION_TOKEN";
    public static final String INVALID_MATCH_RESULT = "INVALID_MATCH_RESULT";
    public static final String NOT_A_PLAYER_OF_LEAGUE = "NOT_A_PLAYER_OF_LEAGUE";
    public static final String ALREADY_A_PLAYER_OF_LEAGUE = "ALREADY_A_PLAYER_OF_LEAGUE";
    public static final String ROUND_DUPLICATE_ERROR = "ROUND_DUPLICATE_ERROR";
    public static final String SEASON_DUPLICATE_ERROR = "SEASON_DUPLICATE_ERROR";

    // EXPIRED
    public static final String EXPIRED_REFRESH_TOKEN = "EXPIRED_REFRESH_TOKEN";
    public static final String EXPIRED_PASSWORD_RESET_TOKEN = "EXPIRED_PASSWORD_RESET_TOKEN";
    public static final String EXPIRED_MAGIC_LINK_TOKEN = "EXPIRED_MAGIC_LINK_TOKEN";
    public static final String EXPIRED_ACCOUNT_ACTIVATION_TOKEN = "EXPIRED_ACCOUNT_ACTIVATION_TOKEN";

    // OTHER
    public static final String OTHER_ERROR = "OTHER_ERROR";
}
