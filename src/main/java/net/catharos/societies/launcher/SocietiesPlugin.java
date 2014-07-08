package net.catharos.societies.launcher;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.core.command.Commands;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.SystemSender;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.database.Database;
import net.catharos.societies.SocietiesModule;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a Launcher
 */
public class SocietiesPlugin extends JavaPlugin {

    private Injector injector;

    private Commands<Sender> commands;
    private MemberProvider<SocietyMember> memberProvider;

    @Override
    public void onEnable() {
        injector = Guice.createInjector(new SocietiesModule());

        commands = injector.getInstance(Key.get(new TypeLiteral<Commands<Sender>>() {}));
        memberProvider = injector.getInstance(Key.get(new TypeLiteral<MemberProvider<SocietyMember>>() {}));
    }

    @Override
    public void onDisable() {
        ListeningExecutorService service = injector.getInstance(ListeningExecutorService.class);

        try {
            service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        service.shutdown();

        Database database = injector.getInstance(Database.class);
        database.close();
    }


    @Override
    public boolean onCommand(CommandSender sender, final Command command, String label, final String[] args) {
        try {
            if (sender instanceof Player) {

                ListenableFuture<SocietyMember> future = memberProvider.getMember(((Player) sender).getUniqueId());

                addCallback(future, new FutureCallback<SocietyMember>() {
                    @Override
                    public void onSuccess(@Nullable SocietyMember result) {
                        try {
                            commands.execute(result, command.getName(), args);
                        } catch (ParsingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });



            } else {
                commands.execute(new SystemSender(), command.getName(), args);
            }

        } catch (ParsingException e) {
            sender.sendMessage(e.getMessage());
        }

        return true;
    }
}
