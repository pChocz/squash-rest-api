package com.pj.squashrestapp.model.entityvisitor;

import java.util.Map;

@SuppressWarnings({"rawtypes", "JavaDoc"})
public class EntityContext {

  private final Map<ClassId, Object> visitedMap;

  public EntityContext(final Map<ClassId, Object> visitedMap) {
    this.visitedMap = visitedMap;
  }

  public boolean isVisited(final ClassId<?> classId) {
    return visitedMap.containsKey(classId);
  }

  public <T> void visit(final ClassId<T> classId, final T object) {
    visitedMap.put(classId, object);
  }

  public <T> T getObject(final ClassId<T> classId) {
    final Object object = visitedMap.get(classId);
    return classId.getClazz().cast(object);
  }

}
