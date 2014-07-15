package net.catharos.societies.launcher;

import com.google.common.io.Files;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.lib.core.command.Commands;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.reflect.instance.CommandAnalyser;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.database.Database;
import net.catharos.societies.SocietiesModule;
import net.catharos.societies.member.SocietyMember;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Represents a SocietiesMain
 */
public class SocietiesMain {

    public static void main(String[] args) throws ParsingException, SQLException, ExecutionException, InterruptedException {

        Injector injector = Guice.createInjector(new SocietiesModule(Files.createTempDir()));

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


        service.awaitTermination(1000, TimeUnit.MILLISECONDS);
        service.shutdown();

        injector.getInstance(Database.class).close();
    }
}
