package net.catharos.societies.launcher;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.MemberPublisher;
import net.catharos.lib.core.command.Commands;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.reflect.instance.CommandAnalyser;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.logging.LoggingHelper;
import net.catharos.lib.database.Database;
import net.catharos.lib.shank.logging.LoggingModule;
import net.catharos.lib.shank.service.ServiceController;
import net.catharos.lib.shank.service.ServiceModule;
import net.catharos.lib.shank.service.lifecycle.Lifecycle;
import net.catharos.societies.SocietiesModule;
import net.catharos.societies.member.SocietyMember;

import java.io.File;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Represents a SocietiesMain
 */
public class SocietiesMain {

    public static void main(String[] args) throws ParsingException, SQLException, ExecutionException, InterruptedException {

        File dir = Files.createTempDir();
        Injector injector = Guice.createInjector(new ServiceModule(), new LoggingModule(dir, LoggingHelper.createContext(SocietiesMain.class)), new SocietiesModule(dir));

        ServiceController serviceController = injector.getInstance(ServiceController.class);
        serviceController.invoke(Lifecycle.INITIALISING);

        ListeningExecutorService service = injector.getInstance(ListeningExecutorService.class);
        injector.getInstance(Key.get(new TypeLiteral<CommandAnalyser<Sender>>() {}));

        Commands<Sender> instance = injector.getInstance(Key.get(new TypeLiteral<Commands<Sender>>() {}));

        SocietyMember sender = injector.getInstance(SocietyMember.class);
        SocietyMember target = injector.getInstance(SocietyMember.class);

//        instance.execute(sender, "society create 5").get();
//        instance.execute(sender, "society list").get();
//        instance.execute(sender, "society profile -target 5").get();

//        instance.execute(sender, "society list");


        instance.execute(sender, "society");
//        instance.execute(target, "society accept");


        MemberPublisher<SocietyMember> memberProvider = injector
                .getInstance(Key.get(new TypeLiteral<MemberPublisher<SocietyMember>>() {}));

        GroupProvider groupProvider = injector
                .getInstance(GroupProvider.class);

        instance.execute(sender, "society command.create test");

        ListenableFuture<Set<Group>> group = groupProvider.getGroup("test");

        Group onlyElement = Iterables.getOnlyElement(group.get());
        onlyElement.addMember(target);

        memberProvider.publish(target).get();


        service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        service.shutdown();

        injector.getInstance(Database.class).close();
    }
}
