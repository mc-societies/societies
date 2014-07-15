package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import net.catharos.groups.DefaultMember;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

/**
 * Represents a ConsoleSocietyMember
 */
public class SystemSocietyMember extends DefaultMember implements SocietyMember {

    private final LocaleProvider localeProvider;

    @Inject
    public SystemSocietyMember(Provider<UUID> uuid, @Named("default-locale") LocaleProvider localeProvider) {
        super(uuid.get());
        this.localeProvider = localeProvider;
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public Locale getLocale() {
        return localeProvider.provide(this);
    }

    @Nullable
    @Override
    public Player toPlayer() {
        return null;
    }

    @Override
    public void send(String message) {
        System.out.println(message);
    }

    @Override
    public void send(String message, Object... args) {
        System.out.println(String.format(message, args));
    }

    @Override
    public void send(StringBuilder message) {
        System.out.println(message);
    }
}
