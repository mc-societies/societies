package org.societies.sieging;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Represents a ConstantCityFunction
 */
public class ConstantCityFunction implements Function<Integer, Double> {

    @Nullable
    @Override
    public Double apply(Integer input) {
        return (double) (input * 2);
    }
}
