package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.Publisher;
import net.catharos.groups.request.Request;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.PlayerProvider;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Provider;
import java.util.Locale;
import java.util.UUID;

/**
 * Represents a SocietyMember
 */
class BukkitSocietyMember extends DefaultMember implements SocietyMember {

    private Request activeRequest;
    private final PlayerProvider playerProvider;
    private final LocaleProvider localeProvider;
    private final Dictionary<String> directory;

    @Inject
    public BukkitSocietyMember(Provider<UUID> uuid,
                               PlayerProvider playerProvider,
                               LocaleProvider localeProvider,
                               Dictionary<String> directory,
                               @Named("society-publisher") Publisher<Member> societyPublisher) {
        this(uuid.get(), playerProvider, localeProvider, directory, societyPublisher);
    }

    @AssistedInject
    public BukkitSocietyMember(@Assisted UUID uuid,
                               PlayerProvider playerProvider,
                               LocaleProvider localeProvider,
                               Dictionary<String> dictionary,
                               @Named("society-publisher") Publisher<Member> societyPublisher
    ) {
        super(uuid, societyPublisher);
        this.playerProvider = playerProvider;
        this.localeProvider = localeProvider;
        this.directory = dictionary;
    }

    @Override
    public void send(String message) {
        Player player = toPlayer();
        if (player == null) {
            return;
        }

        message = directory.getTranslation(message);

        player.sendMessage(message);
    }

    @Override
    public String getName() {
        Player player = toPlayer();

        if (player != null) {
            return player.getName();
        }

        OfflinePlayer offline = playerProvider.getOfflinePlayer(getUUID());

        if (offline != null) {
            return offline.getName();
        }

        throw new RuntimeException("Player is not online!");
    }

    @Override
    public Locale getLocale() {
        return localeProvider.provide(this);
    }

    @Override
    public void send(String message, Object... args) {
        send(String.format(message, args));
    }

    @Override
    public void send(StringBuilder message) {
        send(message.toString());
    }

    @Override
    @Nullable
    public Player toPlayer() {
        return playerProvider.getPlayer(getUUID());
    }
}
