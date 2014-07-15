package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.request.Request;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.PlayerProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Provider;
import java.util.Locale;
import java.util.UUID;

/**
 * Represents a SocietyMember
 */
public class BukkitSocietyMember extends DefaultMember implements SocietyMember {

    private Request activeRequest;
    private final PlayerProvider<Player> playerProvider;
    private final LocaleProvider localeProvider;
    private final Dictionary<String> directory;

    @Inject
    public BukkitSocietyMember(Provider<UUID> uuid,
                               PlayerProvider<Player> playerProvider,
                               LocaleProvider localeProvider,
                               Dictionary<String> directory) {
        this(uuid.get(), playerProvider, localeProvider, directory);
    }

    @AssistedInject
    public BukkitSocietyMember(@Assisted UUID uuid,
                               PlayerProvider<Player> playerProvider,
                               LocaleProvider localeProvider,
                               Dictionary<String> directory) {
        super(uuid);
        this.playerProvider = playerProvider;
        this.localeProvider = localeProvider;
        this.directory = directory;
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
