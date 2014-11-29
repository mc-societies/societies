package net.catharos.societies.member;

import com.google.inject.Inject;
import net.catharos.bridge.*;
import net.catharos.lib.core.command.Command;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.api.member.SocietyMember;
import net.catharos.societies.member.locale.LocaleProvider;
import net.milkbowl.vault.economy.EconomyResponse;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Represents a ConsoleSocietyMember
 */
class SystemSocietyMember implements SocietyMember, Sender {

    private final LocaleProvider localeProvider;
    private final Dictionary<String> directory;

    @Inject
    public SystemSocietyMember(LocaleProvider localeProvider,
                               Dictionary<String> dictionary) {
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
