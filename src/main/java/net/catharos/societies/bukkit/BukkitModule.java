package net.catharos.societies.bukkit;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.MemberFactory;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.bridge.WorldResolver;
import net.catharos.societies.bridge.Scheduler;
import net.catharos.societies.bukkit.bridge.BukkitSocietyMember;
import net.catharos.societies.member.SocietyMember;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Represents a BukkitModule
 */
public class BukkitModule extends AbstractServiceModule {

    public static final Class<? extends SocietyMember> MEMBER_IMPLEMENTATION = BukkitSocietyMember.class;


    private final Server server;
    private final Plugin plugin;
    private final Economy economy;

    public BukkitModule(Server server, Plugin plugin, Economy economy) {
        this.server = server;
        this.plugin = plugin;
        this.economy = economy;
    }

    @Override
    protected void configure() {
        bindService().to(ListenerService.class);

        bind(Economy.class).toInstance(economy);
        bind(Server.class).toInstance(server);
        bind(Plugin.class).toInstance(plugin);
        bind(BukkitScheduler.class).toInstance(server.getScheduler());
        bind(ConsoleCommandSender.class).toInstance(server.getConsoleSender());
        bindNamed("default-world", World.class).toInstance(server.getWorlds().get(0));

        bind(WorldResolver.class).to(BukkitWorldResolver.class);


        bind(Scheduler.class).to(net.catharos.societies.bukkit.bridge.BukkitScheduler.class);

        install(new FactoryModuleBuilder()
                .implement(SocietyMember.class, MEMBER_IMPLEMENTATION)
                .build(new TypeLiteral<MemberFactory<SocietyMember>>() {}));

        bind(SocietyMember.class).to(MEMBER_IMPLEMENTATION);
    }
}
