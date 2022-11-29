package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pj.squashrestapp.model.LostBall;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/** */
@Getter
public final class LostBallsDto {

    final PlayerDto player;
    final UUID uuid;

    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    final LocalDate date;

    final int count;

    public LostBallsDto(final LostBall lostBall) {
        this.player = new PlayerDto(lostBall.getPlayer());
        this.uuid = lostBall.getUuid();
        this.date = lostBall.getDate();
        this.count = lostBall.getCount();
    }

    @Override
    public String toString() {
        return player + " | " + count;
    }
}
