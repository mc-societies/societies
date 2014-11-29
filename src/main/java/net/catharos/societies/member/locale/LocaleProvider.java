package net.catharos.societies.member.locale;

import net.catharos.bridge.Player;

import java.util.Locale;

/**
 * Represents a LocaleProvider
 */
public interface LocaleProvider {

    Locale provide(Player member);
}
