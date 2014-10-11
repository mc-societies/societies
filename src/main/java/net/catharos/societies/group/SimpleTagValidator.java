package net.catharos.societies.group;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.validate.TagValidator;
import net.catharos.groups.validate.ValidateResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a SimpleTagValidator
 */
public class SimpleTagValidator implements TagValidator {

    private final int maxLength;
    private final int minLength;
    private final List disallowed;

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-OR]");

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
        tag = STRIP_COLOR_PATTERN.matcher(tag.trim()).replaceAll("");

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
