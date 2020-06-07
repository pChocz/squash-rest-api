package com.pj.squashrestapp.model.util;

import lombok.Data;
import lombok.Getter;

@SuppressWarnings("JavaDoc")
@Data
@Getter
public class ClassId<T> {

  private final Class<T> clazz;
  private final Long id;

}
