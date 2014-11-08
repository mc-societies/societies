package net.catharos.societies.bukkit.bridge;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.publisher.MemberCreatedPublisher;
import net.catharos.groups.publisher.MemberGroupPublisher;
import net.catharos.groups.publisher.MemberLastActivePublisher;
import net.catharos.groups.publisher.MemberRankPublisher;
import net.catharos.lib.core.command.Command;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderHelper;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.NameProvider;
import net.catharos.societies.bridge.Inventory;
import net.catharos.societies.bridge.Location;
import net.catharos.societies.bridge.Material;
import net.catharos.societies.bridge.World;
import net.catharos.societies.bukkit.BukkitUtil;
import net.catharos.societies.member.SocietyMember;
import net.catharos.societies.member.locale.LocaleProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Provider;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.UUID;

/**
 * Represents a SocietyMember
 */
public class BukkitSocietyMember extends DefaultMember implements SocietyMember {

    private final Server server;
    private final LocaleProvider localeProvider;
    private final Dictionary<String> directory;
    private final NameProvider nameProvider;
    private final Economy economy;

    @Inject
    public BukkitSocietyMember(Provider<UUID> uuid,
                               LocaleProvider localeProvider,
                               Dictionary<String> directory,
                               MemberGroupPublisher societyPublisher,
                               NameProvider nameProvider,
                               MemberRankPublisher memberRankPublisher,
                               Economy economy,
                               MemberLastActivePublisher lastActivePublisher,
                               MemberCreatedPublisher createdPublisher,
                               Server server) {
        this(uuid
                .get(), localeProvider, directory, economy, societyPublisher, nameProvider, memberRankPublisher, lastActivePublisher, createdPublisher, server);
    }

    @AssistedInject
    public BukkitSocietyMember(@Assisted UUID uuid,
                               LocaleProvider localeProvider,
                               Dictionary<String> dictionary,
                               Economy economy, MemberGroupPublisher societyPublisher,
                               NameProvider nameProvider,
                               MemberRankPublisher memberRankPublisher,
                               MemberLastActivePublisher lastActivePublisher,
                               MemberCreatedPublisher createdPublisher,
                               Server server) {
        super(uuid, societyPublisher, memberRankPublisher, lastActivePublisher, createdPublisher);
        this.localeProvider = localeProvider;
        this.directory = dictionary;
        this.economy = economy;
        this.nameProvider = nameProvider;
        this.server = server;
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
    @Nullable
    public String getName() {
        return nameProvider.getName(getUUID());
    }

    @Override
    public boolean isAvailable() {
        return toPlayer() != null;
    }

    @Override
    public Locale getLocale() {
        return localeProvider.provide(this);
    }

    @Override
    public boolean hasPermission(String permission) {
        Player player = toPlayer();
        return player != null && player.hasPermission(permission);
    }

    @Override
    public void send(String message, Object... args) {
        Player player = toPlayer();
        if (player == null) {
            return;
        }

        BukkitUtil.argumentColorReset(args);

        player.sendMessage(MessageFormat.format(directory.getTranslation(message), args));
    }

    @Override
    public void send(StringBuilder message) {
        send(message.toString());
    }

    @Override
    public <S extends Sender, R> R as(Executor<S, R> executor, Class<S> clazz) {
        return SenderHelper.as(executor, clazz, this);
    }

    @Override
    public boolean hasPermission(Command command) {
        Player player = toPlayer();

        return player != null && (command.getPermission() == null || player.hasPermission(command.getPermission()));
    }

    public Player toPlayer() {
        Player player = server.getPlayer(getUUID());
        if (player == null) {
            throw new RuntimeException("Player not available!");
        }
        return player;
    }

    @Override
    public EconomyResponse withdraw(double amount) {
        return economy.withdrawPlayer(toPlayer(), amount);
    }

    @Override
    public EconomyResponse deposit(double amount) {
        return economy.depositPlayer(toPlayer(), amount);
    }

    @Override
    public double getHealth() {
        Player player = toPlayer();
        if (player == null) {
            throw new RuntimeException("Player not online!");
        }

        return player.getHealth();
    }

    @Override
    public int getFoodLevel() {
        Player player = toPlayer();
        return player.getFoodLevel();
    }

    @Nullable
    @Override
    public Location getLocation() {
        Player player = toPlayer();
        return BukkitWorld.toLocation(player.getLocation());
    }

    @Override
    public World getWorld() {
        return new BukkitWorld(toPlayer().getWorld());
    }

    @Override
    public boolean teleport(Location location) {
        return toPlayer().teleport(BukkitWorld.toBukkitLocation(server, location));
    }

    @Override
    public void sendBlockChange(Location location, Material material, byte b) {
        toPlayer().sendBlockChange(BukkitWorld.toBukkitLocation(server, location), BukkitItemStack
                .toBukkitMaterial(material), b);
    }

    @Override
    public Inventory getInventory() {
        return new BukkitInventory(toPlayer().getInventory());
    }
}
