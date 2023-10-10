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
    private boolean enabled;
    private boolean nonLocked;

    public PlayerDto(final Player player) {
        this.uuid = player.getUuid();
        this.username = player.getUsername();
        this.emoji = player.getEmoji();
        this.enabled = player.isEnabled();
        this.nonLocked = player.isNonLocked();
    }

    public PlayerDto(String uuid, String username, String emoji, boolean enabled, boolean nonLocked) {
        this.uuid = UUID.fromString(uuid);
        this.username = username;
        this.emoji = emoji;
        this.enabled = enabled;
        this.nonLocked = nonLocked;
    }

    public void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }

    @Override
    public String toString() {
        return username;
    }
}
