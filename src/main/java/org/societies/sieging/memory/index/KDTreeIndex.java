package org.societies.sieging.memory.index;

import algs.model.IMultiPoint;
import algs.model.kdtree.KDTree;
import com.google.common.collect.ImmutableSet;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.common.AbstractAttributeIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOption;
import com.googlecode.cqengine.resultset.ResultSet;

import java.util.*;

/**
 * Represents a KDIndex
 */
public class KDTreeIndex<A extends IMultiPoint, O> extends AbstractAttributeIndex<A, O> {

    private final KDTree tree;

    protected KDTreeIndex(int dimensions, SimpleAttribute<O, A> attribute) {
        super(attribute, new HashSet<Class<? extends Query>>() {{
            add(Nearest.class);
        }});

        tree = new KDTree(dimensions);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public ResultSet<O> retrieve(Query<O> query, Map<Class<? extends QueryOption>, QueryOption<O>> queryOptions) {
        Class<?> queryClass = query.getClass();
        if (queryClass.equals(Nearest.class)) {
            final Nearest<O, A> equal = (Nearest<O, A>) query;

            return new ResultSet<O>() {
                @Override
                public Iterator<O> iterator() {
                    PointNode<O> nearest = (PointNode<O>) tree.nearest(equal.getLocation());

                    if (nearest == null) {
                        return ImmutableSet.<O>of().iterator();
                    }

                    return ImmutableSet.of(nearest.obj).iterator();
                }

                @Override
                public boolean contains(O object) {
                    return tree.nearest(equal.getLocation()) != null;
                }

                @Override
                public int size() {
                    return tree.nearest(equal.getLocation()) == null ? 0 : 1;
                }

                @Override
                public int getRetrievalCost() {
                    return 0;
                }

                @Override
                public int getMergeCost() {
                    return 0;
                }
            };
        }

        return null;
    }

    @Override
    public void notifyObjectsAdded(Collection<O> objects) {
        for (O object : objects) {
            tree.insert(new PointNode<O>(object, ((SimpleAttribute<O, A>) attribute).getValue(object)));
        }
    }

    @Override
    public void notifyObjectsRemoved(Collection<O> objects) {

    }

    @Override
    public void notifyObjectsCleared() {
        tree.removeAll();
    }

    @Override
    public void init(Set<O> collection) {
        notifyObjectsAdded(collection);
    }

    private final static class PointNode<O> implements IMultiPoint {
        O obj;
        IMultiPoint point;

        public PointNode(O obj, IMultiPoint point) {
            this.obj = obj;
            this.point = point;
        }

        @Override
        public int dimensionality() {
            return point.dimensionality();
        }

        @Override
        public double getCoordinate(int dx) {
            return point.getCoordinate(dx);
        }

        @Override
        public double distance(IMultiPoint imp) {
            return point.distance(imp);
        }

        @Override
        public double[] raw() {
            return point.raw();
        }
    }

    public static <A extends IMultiPoint, O> KDTreeIndex<A, O> onAttribute(int dimensions, Attribute<O, A> attribute) {
        return new KDTreeIndex<A, O>(dimensions, (SimpleAttribute<O, A>) attribute);
    }
}
