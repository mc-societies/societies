package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.publisher.MemberGroupPublisher;
import net.catharos.lib.core.command.Command;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.member.locale.LocaleProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
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
                               LocaleProvider localeProvider,
                               Dictionary<String> dictionary,
                               MemberGroupPublisher societyPublisher) {
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
        System.out.println(MessageFormat.format(directory.getTranslation(message), args));
    }

    @Override
    public void send(StringBuilder message) {
        System.out.println(directory.getTranslation(message.toString()));
    }

    @Override
    public boolean hasPermission(Command command) {
        return true;
    }
}
