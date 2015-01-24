package org.societies.group;

import com.google.inject.Inject;
import org.shank.config.ConfigSetting;
import org.societies.groups.validate.NameValidator;
import org.societies.groups.validate.ValidateResult;

import java.util.List;

/**
 * Represents a SimpleNameValidator
 */
class SimpleNameValidator implements NameValidator {

    private final int maxLength;
    private final int minLength;
    private final List disallowed;

    @Inject
    public SimpleNameValidator(@ConfigSetting("name.max-length") int maxLength,
                               @ConfigSetting("name.min-length") int minLength,
                               @ConfigSetting("name.disallowed") List<String> disallowed) {
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
