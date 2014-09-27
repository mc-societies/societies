package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.publisher.*;
import net.catharos.lib.core.command.Command;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderHelper;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.member.locale.LocaleProvider;
import net.milkbowl.vault.economy.EconomyResponse;
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
                               MemberGroupPublisher societyPublisher,
                               MemberStatePublisher memberStatePublisher,
                               MemberRankPublisher memberRankPublisher,
                               LastActivePublisher lastActivePublisher,
                               MemberCreatedPublisher createdPublisher) {
        super(uuid.get(), societyPublisher, memberStatePublisher, memberRankPublisher, lastActivePublisher, createdPublisher);
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
}
