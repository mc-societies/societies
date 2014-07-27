package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.Publisher;
import net.catharos.lib.core.i18n.Dictionary;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

/**
 * Represents a ConsoleSocietyMember
 */
class SystemSocietyMember extends DefaultMember implements SocietyMember {

    private final LocaleProvider localeProvider;
    private final Dictionary<String> directory;

    @Inject
    public SystemSocietyMember(Provider<UUID> uuid,
                               @Named("default-locale") LocaleProvider localeProvider,
                               Dictionary<String> dictionary,
                               @Named("society-publisher") Publisher<Member> societyPublisher) {
        super(uuid.get(), societyPublisher);
        this.localeProvider = localeProvider;
        this.directory = dictionary;
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
        System.out.println(directory.getTranslation(message));
    }

    @Override
    public void send(String message, Object... args) {
        System.out.println(String.format(directory.getTranslation(message), args));
    }

    @Override
    public void send(StringBuilder message) {
        System.out.println(directory.getTranslation(message.toString()));
    }
}
