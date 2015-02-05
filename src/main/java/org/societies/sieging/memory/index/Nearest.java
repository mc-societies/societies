package org.societies.sieging.memory.index;

import algs.model.IMultiPoint;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.simple.SimpleQuery;

/**
 * Represents a NearestQuery
 */
public class Nearest<O, A extends IMultiPoint> extends SimpleQuery<O, A> {


    private final IMultiPoint location;

    /**
     * Creates a new {@link com.googlecode.cqengine.query.simple.SimpleQuery} initialized to make assertions on values of the specified attribute
     *
     * @param attribute The attribute on which the assertion is to be made
     * @param location
     */
    public Nearest(Attribute<O, A> attribute, IMultiPoint location) {
        super(attribute);
        this.location = location;
    }


    @Override
    protected boolean matchesSimpleAttribute(SimpleAttribute<O, A> attribute, O object) {
        return false;
    }

    @Override
    protected boolean matchesNonSimpleAttribute(Attribute<O, A> attribute, O object) {
        return false;
    }

    @Override
    protected int calcHashCode() {
        return 0;
    }

    public IMultiPoint getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nearest equal = (Nearest) o;

        return attribute.equals(equal.attribute) && location.equals(equal.location);
    }

    public static <O, A extends IMultiPoint> Nearest<O, A> nearest(Attribute<O, A> attribute, A attributeValue) {
        return new Nearest<O, A>(attribute, attributeValue);
    }
}
