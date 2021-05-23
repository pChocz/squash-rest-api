package com.pj.squashrestapp.config.security.playerregistration;

import com.pj.squashrestapp.model.Player;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/** */
@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

  private String frontendUrl;
  private Locale locale;
  private Player player;

  public OnRegistrationCompleteEvent(
      final Player player, final Locale locale, final String frontendUrl) {
    super(player);
    this.player = player;
    this.locale = locale;
    this.frontendUrl = frontendUrl;
  }
}
