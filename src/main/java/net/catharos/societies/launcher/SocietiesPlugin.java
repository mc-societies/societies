package net.catharos.societies.launcher;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.MemberPublisher;
import net.catharos.lib.core.command.Commands;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.logging.LoggingHelper;
import net.catharos.lib.database.Database;
import net.catharos.lib.shank.logging.LoggingModule;
import net.catharos.lib.shank.service.ServiceController;
import net.catharos.lib.shank.service.ServiceModule;
import net.catharos.lib.shank.service.lifecycle.Lifecycle;
import net.catharos.societies.SocietiesModule;
import net.catharos.societies.bukkit.BukkitModule;
import net.catharos.societies.database.sql.OnlineCacheMemberProvider;
import net.catharos.societies.member.MemberFactory;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Futures.addCallback;


/**
 * Represents a Launcher
 */
public class SocietiesPlugin extends JavaPlugin implements Listener, ReloadAction {

    private Injector injector;

    private Commands<Sender> commands;
    private MemberProvider<SocietyMember> memberProvider;
    private ServiceController serviceController;

    @Override
    public void onLoad() {
        //getServer() -> null
    }

    @Override
    public void onEnable() {

        File dir = getDataFolder();
        injector = Guice.createInjector(
                new ServiceModule(),
                new LoggingModule(dir, LoggingHelper.createContext(SocietiesMain.class)),
                new SocietiesModule(dir),
                new BukkitModule(getServer())
        );
        serviceController = injector.getInstance(ServiceController.class);

        serviceController.invoke(Lifecycle.INITIALISING);


        getServer().getPluginManager().registerEvents(this, this);
        commands = injector.getInstance(Key.get(new TypeLiteral<Commands<Sender>>() {}));
        memberProvider = injector.getInstance(Key.get(new TypeLiteral<MemberProvider<SocietyMember>>() {}));

        serviceController.invoke(Lifecycle.STARTING);
    }

    @Override
    public void onDisable() {
        serviceController.invoke(Lifecycle.STOPPING);
        ListeningExecutorService service = injector.getInstance(ListeningExecutorService.class);

        try {
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // Nobody fucking cares!
            e.printStackTrace();
        }

        service.shutdown();

        Database database = injector.getInstance(Database.class);
        database.close();
    }


    @Override
    public boolean onCommand(CommandSender sender, final Command command, String label, final String[] args) {

        if (sender instanceof Player) {

            ListenableFuture<SocietyMember> future = memberProvider.getMember(((Player) sender).getUniqueId());

            addCallback(future, new FutureCallback<SocietyMember>() {
                @Override
                public void onSuccess(@Nullable SocietyMember result) {
                    commands.execute(result, command.getName(), args);
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                }
            });


        } else {
            commands.execute(injector.getInstance(Key.get(Sender.class, Names.named("system-sender"))), command
                    .getName(), args);
        }


        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        MemberPublisher<SocietyMember> publisher = injector
                .getInstance(Key.get(new TypeLiteral<MemberPublisher<SocietyMember>>() {}));

        MemberFactory memberFactory = injector
                .getInstance(MemberFactory.class);

        publisher.publish(memberFactory.create(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        OnlineCacheMemberProvider cache = injector.getInstance(OnlineCacheMemberProvider.class);
        cache.clear(event.getPlayer().getUniqueId());
    }

    @Override
    public void reload() {
        onDisable();
        onEnable();
    }
}
