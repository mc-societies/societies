package org.societies.group;

import com.google.inject.Inject;
import net.catharos.lib.shank.config.ConfigSetting;
import org.societies.bridge.ChatColor;
import org.societies.groups.validate.TagValidator;
import org.societies.groups.validate.ValidateResult;

import java.util.List;

/**
 * Represents a SimpleTagValidator
 */
public class SimpleTagValidator implements TagValidator {

    private final int maxLength;
    private final int minLength;
    private final List disallowed;

    @Inject
    public SimpleTagValidator(@ConfigSetting("tag.max-length") int maxLength,
                              @ConfigSetting("tag.min-length") int minLength,
                              @ConfigSetting("tag.disallowed") List<String> disallowed) {
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
