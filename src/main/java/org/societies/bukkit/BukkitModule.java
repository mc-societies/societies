package org.societies.bukkit;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import net.milkbowl.vault.economy.Economy;
import order.SystemSender;
import order.sender.Sender;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.shank.service.AbstractServiceModule;
import org.societies.api.NameProvider;
import org.societies.api.PlayerResolver;
import org.societies.api.ReloadAction;
import org.societies.bukkit.converter.ConverterModule;
import org.societies.bukkit.listener.ListenerService;
import org.societies.converter.ConverterService;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.member.Member;

import java.util.UUID;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

/**
 * Represents a BukkitModule
 */
public class BukkitModule extends AbstractServiceModule {

    private final Server server;
    private final SocietiesPlugin plugin;
    private final Economy economy;

    public BukkitModule(Server server, SocietiesPlugin plugin, Economy economy) {
        this.server = server;
        this.plugin = plugin;
        this.economy = economy;
    }

    @Override
    protected void configure() {
        bindService().to(ListenerService.class);

        bindService().to(SchedulerService.class);

        bindNamed("version", String.class).toInstance(plugin.getDescription().getVersion());

        bind(Economy.class).toInstance(economy);
        bind(Server.class).toInstance(server);
        bind(Plugin.class).toInstance(plugin);
        bind(BukkitScheduler.class).toInstance(server.getScheduler());
        bind(ConsoleCommandSender.class).toInstance(server.getConsoleSender());
        bindNamed("default-world", World.class).toInstance(server.getWorlds().get(0));


        bind(BukkitScheduler.class).toInstance(server.getScheduler());

        bind(NameProvider.class).to(BukkitNameProvider.class);

        bind(ReloadAction.class).toInstance(new ReloadAction() {
            @Override
            public void reload() {
                plugin.onDisable();
                plugin.onLoad();
                plugin.onEnable();
            }
        });

        bind(PlayerResolver.class).to(BukkitPlayerResolver.class);

        bind(SystemSender.class).to(BukkitSystemSender.class);

        install(new ConverterModule(server));
        bindService().to(ConverterService.class);

        bind(ClassLoader.class).toInstance(plugin.getPluginClassLoader());

        Multibinder<ExtensionRoller<Member>> extensions = newSetBinder(
                binder(),
                new TypeLiteral<ExtensionRoller<Member>>() {}
        );

        install(new FactoryModuleBuilder()
                .implement(Sender.class, BukkitSocietiesMember.class)
                .build(new TypeLiteral<ExtensionFactory<Sender, UUID>>() {}));

        install(new FactoryModuleBuilder()
                .implement(BukkitSocietiesMember.class, BukkitSocietiesMember.class)
                .build(new TypeLiteral<ExtensionFactory<BukkitSocietiesMember, UUID>>() {}));

        extensions.addBinding().to(BukkitExtensionRoller.class);

        install(new ConverterModule(server));
    }
}
