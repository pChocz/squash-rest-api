package com.pj.squashrestapp.dto;

import com.pj.squashrestapp.model.Player;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerDto {

    @EqualsAndHashCode.Include
    private UUID uuid;

    private String username;
    private String emoji;

    public PlayerDto(final Player player) {
        this.uuid = player.getUuid();
        this.username = player.getUsername();
        this.emoji = player.getEmoji();
    }

    @Override
    public String toString() {
        return username;
    }
}
