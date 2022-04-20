package com.pj.squashrestapp.dbinit.jsondto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class JsonRefreshToken {

    @JsonFormat(pattern = GeneralUtil.DATE_TIME_FORMAT)
    private LocalDateTime expirationDateTime;

    private UUID playerUuid;
    private UUID token;
}
