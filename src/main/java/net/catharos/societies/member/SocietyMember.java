package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import net.catharos.groups.DefaultMember;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.request.Participant;
import net.catharos.societies.request.Request;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Provider;
import java.util.UUID;

/**
 * Represents a SocietyMember
 */
public class SocietyMember extends DefaultMember implements Sender, Participant {

    private Request activeRequest;
    private final PlayerProvider<Player> playerProvider;

    @Inject
    public SocietyMember(Provider<UUID> uuid, PlayerProvider<Player> playerProvider) {
        this(uuid.get(), playerProvider);
    }

    @AssistedInject
    public SocietyMember(@Assisted UUID uuid, PlayerProvider<Player> playerProvider) {
        super(uuid);
        this.playerProvider = playerProvider;
    }

    @Override
    public void send(String message) {
//        Player player = toPlayer();
//        if (player == null) {
//            return;
//        }
//
//        player.sendMessage(message);
        System.out.println(message);
    }

    @Override
    public void send(String message, Object... args) {
        send(String.format(message, args));
    }

    @Override
    public void send(StringBuilder message) {
        send(message.toString());
    }

    @Nullable
    public Player toPlayer() {
        return playerProvider.getPlayer(getUUID());
    }

    @Nullable
    @Override
    public Request getActiveRequest() {
        return activeRequest;
    }

    @Override
    public void setActiveRequest(Request activeRequest) {
        this.activeRequest = activeRequest;
    }

    @Override
    public boolean clearRequest() {
        boolean value = activeRequest != null;
        activeRequest = null;
        return value;
    }
}
