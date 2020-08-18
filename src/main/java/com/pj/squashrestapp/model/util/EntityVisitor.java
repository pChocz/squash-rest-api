package com.pj.squashrestapp.model.util;

import lombok.Getter;

import java.util.Set;

@Getter
@SuppressWarnings({"unchecked", "MethodWithMoreThanThreeNegations", "JavaDoc"})
public abstract class EntityVisitor<T extends Identifiable, P extends Identifiable> {

  private final Class<T> targetClass;

  public EntityVisitor(final Class<T> targetClass) {
    this.targetClass = targetClass;
  }

  public void visit(final T object, final EntityContext entityContext) {
    final Class<T> clazz = (Class<T>) object.getClass();
    final ClassId<T> objectClassId = new ClassId<>(clazz, object.getId());
    final boolean objectVisited = entityContext.isVisited(objectClassId);
    if (!objectVisited) {
      entityContext.visit(objectClassId, object);
    }
    final P parent = getParent(object);
    if (parent != null) {
      final Class<P> parentClass = (Class<P>) parent.getClass();
      final ClassId<P> parentClassId = new ClassId<>(parentClass, parent.getId());
      if (!entityContext.isVisited(parentClassId)) {
        setChildren(parent);
      }
      final Set<T> children = getChildren(parent);
      if (!objectVisited) {
        children.add(object);
      }
    }

  }

  public P getParent(final T visitingObject) {
    return null;
  }

  public void setChildren(final P parent) {
    throw new UnsupportedOperationException();
  }

  public Set<T> getChildren(final P parent) {
    throw new UnsupportedOperationException();
  }

}
