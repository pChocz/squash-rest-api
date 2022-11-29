package com.pj.squashrestapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.pj.squashrestapp.model.BonusPoint;
import com.pj.squashrestapp.model.audit.Audit;
import com.pj.squashrestapp.util.GeneralUtil;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

/** */
@Getter
public final class BonusPointsDto {

    final PlayerDto winner;
    final PlayerDto looser;
    final UUID uuid;

    @JsonFormat(pattern = GeneralUtil.DATE_FORMAT)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    final LocalDate date;

    final int points;
    final Audit audit;

    public BonusPointsDto(final BonusPoint bonusPoint) {
        this.winner = new PlayerDto(bonusPoint.getWinner());
        this.looser = new PlayerDto(bonusPoint.getLooser());
        this.uuid = bonusPoint.getUuid();
        this.date = bonusPoint.getDate();
        this.points = bonusPoint.getPoints();
        this.audit = bonusPoint.getAudit();
    }

    @Override
    public String toString() {
        return winner + " | " + looser + " | " + points;
    }
}
