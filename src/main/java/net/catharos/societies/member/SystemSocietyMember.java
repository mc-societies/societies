package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.catharos.groups.DefaultMember;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a ConsoleSocietyMember
 */
public class SystemSocietyMember extends DefaultMember implements SocietyMember {

    @Inject
    public SystemSocietyMember(Provider<UUID> uuid) {
        super(uuid.get());
    }

    @Override
    public String getName() {
        return "Console";
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
