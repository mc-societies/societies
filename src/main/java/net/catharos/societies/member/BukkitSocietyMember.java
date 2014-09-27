package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.publisher.*;
import net.catharos.lib.core.command.Command;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderHelper;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.NameProvider;
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.member.locale.LocaleProvider;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Provider;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.UUID;

/**
 * Represents a SocietyMember
 */
class BukkitSocietyMember extends DefaultMember implements SocietyMember {

    private final PlayerProvider playerProvider;
    private final LocaleProvider localeProvider;
    private final Dictionary<String> directory;
    private final NameProvider nameProvider;
    private final Economy economy;

    @Inject
    public BukkitSocietyMember(Provider<UUID> uuid,
                               PlayerProvider playerProvider,
                               LocaleProvider localeProvider,
                               Dictionary<String> directory,
                               MemberGroupPublisher societyPublisher,
                               NameProvider nameProvider,
                               MemberStatePublisher memberStatePublisher,
                               MemberRankPublisher memberRankPublisher,
                               Economy economy,
                               LastActivePublisher lastActivePublisher,
                               MemberCreatedPublisher createdPublisher) {
        this(uuid
                .get(), playerProvider, localeProvider, directory, economy, societyPublisher, nameProvider, memberStatePublisher, memberRankPublisher, lastActivePublisher, createdPublisher);
    }

    @AssistedInject
    public BukkitSocietyMember(@Assisted UUID uuid,
                               PlayerProvider playerProvider,
                               LocaleProvider localeProvider,
                               Dictionary<String> dictionary,
                               Economy economy, MemberGroupPublisher societyPublisher,
                               NameProvider nameProvider,
                               MemberStatePublisher memberStatePublisher,
                               MemberRankPublisher memberRankPublisher,
                               LastActivePublisher lastActivePublisher,
                               MemberCreatedPublisher createdPublisher) {
        super(uuid, societyPublisher, memberStatePublisher, memberRankPublisher, lastActivePublisher, createdPublisher);
        this.playerProvider = playerProvider;
        this.localeProvider = localeProvider;
        this.directory = dictionary;
        this.economy = economy;
        this.nameProvider = nameProvider;
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
        if (player == null) {
            return false;
        }

        return player.hasPermission(permission);
    }

    @Override
    public void send(String message, Object... args) {
        Player player = toPlayer();
        if (player == null) {
            return;
        }

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

    @Override
    @Nullable
    public Player toPlayer() {
        return playerProvider.getPlayer(getUUID());
    }

    @Override
    public EconomyResponse withdraw(double amount) {
        return economy.withdrawPlayer(toPlayer(), amount);
    }

    @Override
    public EconomyResponse deposit(double amount) {
        return economy.depositPlayer(toPlayer(), amount);
    }
}
