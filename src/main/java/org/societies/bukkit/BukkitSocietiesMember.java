package org.societies.bukkit;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.milkbowl.vault.economy.Economy;
import order.Command;
import order.sender.Sender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.societies.api.economy.EconomyParticipant;
import org.societies.api.economy.EconomyResponse;
import org.societies.groups.dictionary.Dictionary;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Represents a SocietyMember
 */
public class BukkitSocietiesMember implements EconomyParticipant, Sender {
    private final Dictionary<String> directory;

    private final Economy economy;
    private final UUID uuid;
    private final Plugin plugin;

    @Inject
    public BukkitSocietiesMember(@Assisted UUID uuid,
                                 Dictionary<String> directory,
                                 Economy economy,
                                 Plugin plugin) {
        this.directory = directory;
        this.economy = economy;
        this.uuid = uuid;
        this.plugin = plugin;
    }

    @Override
    public void send(String message) {
        Player player = toPlayer();
        if (player == null) {
            return;
        }

        message = directory.getTranslation(message);

        if (message.length() > Short.MAX_VALUE) {
            player.sendMessage("Message way too long!");
            System.out.println(message);
            return;
        }

        player.sendMessage(message);
    }

    @Override
    @Nullable
    public String getName() {
        return getServer().getOfflinePlayer(uuid).getName();
    }

    private Server getServer() {
        return Bukkit.getServer();
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

        player.sendMessage(directory.getTranslation(message, args));
    }

    @Override
    public void send(StringBuilder message) {
        send(message.toString());
    }

    @Override
    public boolean hasPermission(Command command) {
        Player player = toPlayer();

        return player != null && (command.getPermission() == null || player.hasPermission(command.getPermission()));
    }

    @Nullable
    public Player toPlayer() {
        return getServer().getPlayer(uuid);
    }

    public Player toPlayerNotNull() {
        Player player = getServer().getPlayer(uuid);
        if (player == null) {
            throw new RuntimeException("Player not available!");
        }
        return player;
    }

    public OfflinePlayer toOfflinePlayer() {
        OfflinePlayer player = getServer().getOfflinePlayer(uuid);
        if (player == null) {
            throw new RuntimeException("Player not available!");
        }
        return player;
    }

    @Override
    public EconomyResponse withdraw(final double amount) {
        try {
            return Bukkit.getScheduler().callSyncMethod(plugin, new Callable<EconomyResponse>() {
                @Override
                public EconomyResponse call() throws Exception {
                    net.milkbowl.vault.economy.EconomyResponse response = economy.withdrawPlayer(toPlayer(), amount);
                    return new EconomyResponse(response.amount, response.balance, response.transactionSuccess());
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new EconomyResponse(0, 0, false);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new EconomyResponse(0, 0, false);
        }
    }

    @Override
    public EconomyResponse deposit(final double amount) {
        try {
            return Bukkit.getScheduler().callSyncMethod(plugin, new Callable<EconomyResponse>() {
                @Override
                public EconomyResponse call() throws Exception {
                    net.milkbowl.vault.economy.EconomyResponse response = economy.depositPlayer(toPlayer(), amount);
                    return new EconomyResponse(response.amount, response.balance, response.transactionSuccess());
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new EconomyResponse(0, 0, false);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new EconomyResponse(0, 0, false);
        }
    }
}
