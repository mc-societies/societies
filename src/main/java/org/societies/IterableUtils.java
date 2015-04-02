package org.societies;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Represents a IterableUtils
 */
public class IterableUtils {


    public static <T> String toString(Iterable<T> iterable, Function<T, String> function) {
        if (Iterables.isEmpty(iterable)) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (T obj : iterable) {
            String string = function.apply(obj);

            builder.append(string).append(", ");
        }

        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }
}
