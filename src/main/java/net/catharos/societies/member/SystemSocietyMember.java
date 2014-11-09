package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.publisher.MemberCreatedPublisher;
import net.catharos.groups.publisher.MemberGroupPublisher;
import net.catharos.groups.publisher.MemberLastActivePublisher;
import net.catharos.groups.publisher.MemberRankPublisher;
import net.catharos.lib.core.command.Command;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderHelper;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.bridge.*;
import net.catharos.societies.member.locale.LocaleProvider;
import net.milkbowl.vault.economy.EconomyResponse;
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
                               MemberGroupPublisher societyPublisher,
                               MemberRankPublisher memberRankPublisher,
                               MemberLastActivePublisher lastActivePublisher,
                               MemberCreatedPublisher createdPublisher) {
        super(uuid.get(), societyPublisher, memberRankPublisher, lastActivePublisher, createdPublisher);
        this.localeProvider = localeProvider;
        this.directory = dictionary;
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return localeProvider.provide(this);
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void send(String message) {
        System.out.println(directory.getTranslation(message));
    }

    @Override
    public void send(String message, Object... args) {
        ChatColor.argumentColorReset(args);
        System.out.println(MessageFormat.format(directory.getTranslation(message), args));
    }

    @Override
    public void send(StringBuilder message) {
        System.out.println(directory.getTranslation(message.toString()));
    }

    @Override
    public <S extends Sender, R> R as(Executor<S, R> executor, Class<S> clazz) {
        return SenderHelper.as(executor, clazz, this);
    }

    @Override
    public boolean hasPermission(Command command) {
        return true;
    }

    @Override
    public EconomyResponse withdraw(double amount) {
        return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse deposit(double amount) {
        return withdraw(amount);
    }

    @Override
    public double getHealth() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getFoodLevel() {
        return Integer.MAX_VALUE;
    }

    @Nullable
    @Override
    public Location getLocation() {
        return new Location(null, 0, 0, 0);
    }

    @Override
    public World getWorld() {
        return null;
    }

    @Override
    public boolean teleport(Location location) {
        return false;
    }

    @Override
    public void sendBlockChange(Location location, Material material, byte b) {

    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
