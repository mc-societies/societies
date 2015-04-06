package org.societies.member.locale;


import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Represents a LocaleProvider
 */
public interface LocaleProvider {

    Locale provide(Player member);
}
