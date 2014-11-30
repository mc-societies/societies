package org.societies.member.locale;

import org.societies.bridge.Player;

import java.util.Locale;

/**
 * Represents a LocaleProvider
 */
public interface LocaleProvider {

    Locale provide(Player member);
}
