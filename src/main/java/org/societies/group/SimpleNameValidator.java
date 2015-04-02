package org.societies.group;

import com.google.inject.Inject;
import org.shank.config.ConfigSetting;
import org.societies.groups.dictionary.Dictionary;
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
    private final Dictionary<String> dictionary;

    @Inject
    public SimpleNameValidator(@ConfigSetting("name.max-length") int maxLength,
                               @ConfigSetting("name.min-length") int minLength,
                               @ConfigSetting("name.disallowed") List<String> disallowed,
                               Dictionary<String> dictionary) {
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.disallowed = disallowed;
        this.dictionary = dictionary;
    }

    @Override
    public ValidateResult validateName(String name) {
        name = name.trim();

        int length = name.length();

        if (length > maxLength) {
            return new ValidateResult(dictionary.getTranslation("society.name.too-long", maxLength), false);
        }

        if (length < minLength) {
            return new ValidateResult(dictionary.getTranslation("society.name.too-short", minLength), false);
        }

        if (disallowed.contains(name)) {
            return new ValidateResult(dictionary.getTranslation("society.name.disallowed", name), false);
        }

        return new ValidateResult("society.name.success", true);
    }
}
