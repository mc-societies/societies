package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Represents a DynamicLocaleProvider
 */
class DynamicLocaleProvider implements LocaleProvider {

    private final LocaleProvider localeProvider;

    @Inject
    public DynamicLocaleProvider(@Named("default-locale") LocaleProvider localeProvider) {this.localeProvider = localeProvider;}

    @Override
    public Locale provide(SocietyMember member) {
        Player player = member.toPlayer();

        if (player == null) {
            return localeProvider.provide(member);
        }

        try {
            return new Locale(player.spigot().getLocale());
        } catch (NoSuchMethodError e) {
            return localeProvider.provide(member);
        }
    }
}
