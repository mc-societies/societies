package net.catharos.societies;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
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
import java.util.concurrent.Executors;

/**
 * Represents a SocietiesModule
 */
public class SocietiesModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        bindService().to(SocietiesService.class);

        bind(UUID.class).toProvider(TimeUUIDProvider.class);
        bind(Dictionary.class).toInstance(new DefaultDictionary(Locale.getDefault())); //fixme correct dictionary

        install(new DatabaseModule("localhost", "catharos", "root", "", 3306));

        install(new CommandModule());

        bind(Thread.UncaughtExceptionHandler.class).toInstance(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });

        bind(new TypeLiteral<PlayerProvider<Player>>() {}).to(BukkitPlayerProvider.class);

        install(new SocietyModule());
        install(new MemberModule());

        bind(ListeningExecutorService.class)
                .toInstance(MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2)));

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
