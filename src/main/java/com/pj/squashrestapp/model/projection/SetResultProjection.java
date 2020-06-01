package com.pj.squashrestapp.model.projection;

import com.pj.squashrestapp.model.SetResult;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "setResultProjection", types = {SetResult.class})
@SuppressWarnings({"JavaDoc", "unused"})
public interface SetResultProjection {

  int getNumber();

  int getFirstPlayerScore();

  int getSecondPlayerScore();

}
