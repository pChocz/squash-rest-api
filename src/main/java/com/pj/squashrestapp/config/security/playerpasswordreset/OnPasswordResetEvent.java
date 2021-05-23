package com.pj.squashrestapp.config.security.playerpasswordreset;

import com.pj.squashrestapp.model.Player;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/** */
@Getter
@Setter
public class OnPasswordResetEvent extends ApplicationEvent {

  private String frontendUrl;
  private Locale locale;
  private Player player;

  public OnPasswordResetEvent(final Player player, final Locale locale, final String frontendUrl) {
    super(player);
    this.player = player;
    this.locale = locale;
    this.frontendUrl = frontendUrl;
  }
}
