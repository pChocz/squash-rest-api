package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class JsonPlayerCredentials {

    private String username;
    private String emoji;
    private String password;
    private String passwordHashed;
    private String email;
    private UUID uuid;
    private UUID passwordSessionUuid;
    private boolean enabled;
    private Boolean wantsEmails;
    private String locale;
    private List<JsonLeagueRoles> leagueRoles;
    private List<JsonAuthorities> authorities;
    private boolean nonLocked;
    private Long successfulLoginAttempts;

    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    private LocalDateTime registrationDateTime;

    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    private LocalDateTime lastLoggedInDateTime;
}
