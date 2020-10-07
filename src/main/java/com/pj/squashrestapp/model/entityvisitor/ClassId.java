package com.pj.squashrestapp.model.entityvisitor;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
@SuppressWarnings("JavaDoc")
public class ClassId<T> {

  private final Class<T> clazz;
  private final Long id;

}
