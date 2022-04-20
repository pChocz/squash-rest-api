package com.pj.squashrestapp.model.entityvisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked", "JavaDoc"})
public class EntityGraphBuilder {

    private final Map<Class, EntityVisitor> visitorsMap;
    private final EntityContext entityContext;

    public EntityGraphBuilder(final EntityVisitor[] entityVisitors) {
        visitorsMap = new HashMap<>();
        for (final EntityVisitor entityVisitor : entityVisitors) {
            visitorsMap.put(entityVisitor.getTargetClass(), entityVisitor);
        }
        entityContext = new EntityContext(new HashMap<>());
    }

    public EntityContext getEntityContext() {
        return entityContext;
    }

    public EntityGraphBuilder build(final List<? extends Identifiable> objects) {
        for (final Identifiable object : objects) {
            visit(object);
        }
        return this;
    }

    private <T extends Identifiable, P extends Identifiable> void visit(final T object) {
        final Class<T> clazz = (Class<T>) object.getClass();
        final EntityVisitor<T, P> entityVisitor = visitorsMap.get(clazz);
        if (entityVisitor == null) {
            throw new IllegalArgumentException("Class " + clazz + " has no entityVisitor!");
        }
        entityVisitor.visit(object, entityContext);
        final P parent = entityVisitor.getParent(object);
        if (parent != null) {
            visit(parent);
        }
    }
}
