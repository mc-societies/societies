package net.catharos.societies;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import net.catharos.lib.core.command.format.DefaultFormatter;
import net.catharos.lib.core.command.format.Formatter;
import net.catharos.lib.core.command.format.MonospacedWidthProvider;
import net.catharos.lib.core.command.format.WidthProvider;
import net.catharos.lib.core.command.format.table.*;
import net.catharos.lib.core.i18n.DefaultDictionary;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.bukkit.BukkitPlayerProvider;
import net.catharos.societies.commands.CommandModule;
import net.catharos.societies.database.DatabaseModule;
import net.catharos.societies.group.SocietyModule;
import net.catharos.societies.member.MemberModule;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Represents a SocietiesModule
 */
public class SocietiesModule extends AbstractServiceModule {

    @Override
    protected void configure() {

        // Register service
        bindService().to(SocietiesService.class);

        // UUID provider
        bind(UUID.class).toProvider(TimeUUIDProvider.class);

        // Directory
        bind(Dictionary.class).toInstance(new DefaultDictionary(Locale.getDefault())); //fixme correct dictionary

        // Database
        install(new DatabaseModule("localhost", "catharos", "root", "", 3306));

        // Commands
        install(new CommandModule());

        // Members
        install(new MemberModule());

        // Societies
        install(new SocietyModule());


        // Global stuff
        bind(Thread.UncaughtExceptionHandler.class).toInstance(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });

        // Player provider
        bind(new TypeLiteral<PlayerProvider<Player>>() {}).to(BukkitPlayerProvider.class);


        // Executor service for heavy work
        bind(ListeningExecutorService.class).toInstance(listeningDecorator(newFixedThreadPool(2)));

        // Chat rendering
        bind(Table.class).to(DefaultTable.class);
        bind(WidthProvider.class).toInstance(new MonospacedWidthProvider(5));

        install(new FactoryModuleBuilder()
                .implement(Row.class, Names.named("default"), DefaultRow.class)
                .implement(Row.class, Names.named("forward"), ForwardingRow.class)
                .build(RowFactory.class));

        bindNamedInstance("column-spacing", double.class, 12.0D);
        bindNamedInstance("max-line-length", double.class, 315.0D);
        bind(Formatter.class).to(DefaultFormatter.class);
    }

}
