package net.catharos.societies.group;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.validate.TagValidator;
import net.catharos.groups.validate.ValidateResult;
import net.catharos.societies.bridge.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a SimpleTagValidator
 */
public class SimpleTagValidator implements TagValidator {

    private final int maxLength;
    private final int minLength;
    private final List disallowed;

    @Inject
    public SimpleTagValidator(@Named("tag.max-length") Integer maxLength,
                              @Named("tag.min-length") Integer minLength,
                              @Named("tag.disallowed") ArrayList disallowed) {
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.disallowed = disallowed;
    }

    @Override
    public ValidateResult validateTag(String tag) {
        tag = ChatColor.stripUserColor(tag.trim());

        int length = tag.length();

        if (length > maxLength) {
            return new ValidateResult("society.tag.too-long", false);
        }

        if (length < minLength) {
            return new ValidateResult("society.tag.too-short", false);
        }

        if (disallowed.contains(tag)) {
            return new ValidateResult("society.tag.disallowed", false);
        }

        return new ValidateResult("society.tag.success", true);
    }
}
