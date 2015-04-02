package org.societies.group;

import com.google.inject.Inject;
import org.shank.config.ConfigSetting;
import org.societies.bridge.ChatColor;
import org.societies.groups.dictionary.Dictionary;
import org.societies.groups.validate.TagValidator;
import org.societies.groups.validate.ValidateResult;

import java.util.List;

/**
 * Represents a SimpleTagValidator
 */
class SimpleTagValidator implements TagValidator {

    private final int maxLength;
    private final int minLength;
    private final List disallowed;
    private final Dictionary<String> dictionary;

    @Inject
    public SimpleTagValidator(@ConfigSetting("tag.max-length") int maxLength,
                              @ConfigSetting("tag.min-length") int minLength,
                              @ConfigSetting("tag.disallowed") List<String> disallowed, Dictionary<String> dictionary) {
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.disallowed = disallowed;
        this.dictionary = dictionary;
    }

    @Override
    public ValidateResult validateTag(String tag) {
        tag = ChatColor.stripUserColor(tag.trim());

        int length = tag.length();

        if (length > maxLength) {
            return new ValidateResult(dictionary.getTranslation("society.tag.too-long", maxLength), false);
        }

        if (length < minLength) {
            return new ValidateResult(dictionary.getTranslation("society.tag.too-short", minLength), false);
        }

        if (disallowed.contains(tag)) {
            return new ValidateResult(dictionary.getTranslation("society.tag.disallowed", tag), false);
        }

        return new ValidateResult("society.tag.success", true);
    }
}
