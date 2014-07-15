package net.catharos.societies.member;

import java.util.Locale;

/**
 * Represents a LocaleProvider
 */
public interface LocaleProvider {

    Locale provide(SocietyMember member);
}
