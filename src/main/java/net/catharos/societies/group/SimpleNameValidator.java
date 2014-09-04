package net.catharos.societies.group;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.validate.NameValidator;
import net.catharos.groups.validate.ValidateResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SimpleNameValidator
 */
public class SimpleNameValidator implements NameValidator {

    private final int maxLength;
    private final int minLength;
    private final List disallowed;

    @Inject
    public SimpleNameValidator(@Named("name.max-length") Integer maxLength,
                               @Named("name.min-length") Integer minLength,
                               @Named("name.disallowed") ArrayList disallowed) {
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.disallowed = disallowed;
    }

    @Override
    public ValidateResult validateName(String name) {
        name = name.trim();

        int length = name.length();

        if (length > maxLength) {
            return new ValidateResult("society.name.too-long", false);
        }

        if (length < minLength) {
            return new ValidateResult("society.name.too-short", false);
        }

        if (disallowed.contains(name)) {
            return new ValidateResult("society.name.disallowed", false);
        }

        return new ValidateResult("society.name.success", true);
    }
}
