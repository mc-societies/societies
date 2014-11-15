package net.catharos.societies.member.locale;

import net.catharos.societies.api.member.SocietyMember;

import java.util.Locale;

/**
 * Represents a LocaleProvider
 */
public interface LocaleProvider {

    Locale provide(SocietyMember member);
}
