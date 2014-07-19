package net.catharos.societies;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Singleton;
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
import net.catharos.lib.core.i18n.MutableDictionary;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import net.catharos.lib.shank.config.ConfigModule;
import net.catharos.lib.shank.config.JSONSource;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.bukkit.BukkitPlayerProvider;
import net.catharos.societies.commands.CommandModule;
import net.catharos.societies.database.DatabaseModule;
import net.catharos.societies.group.SocietyModule;
import net.catharos.societies.member.DynamicLocaleProvider;
import net.catharos.societies.member.LocaleProvider;
import net.catharos.societies.member.MemberModule;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Represents a SocietiesModule
 */
public class SocietiesModule extends AbstractServiceModule {

    private final File dataDirectory;

    public SocietiesModule(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    protected void configure() {
        // Configuration
        try {
            prepareDefaults(dataDirectory);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Logging
        bindNamed("service-logger", Logger.class).toInstance(LogManager.getLogger());

        // Locale
        bind(LocaleProvider.class).to(DynamicLocaleProvider.class);
        bindNamed("default-locale", LocaleProvider.class).to(DynamicLocaleProvider.class);
        bindNamedInstance("default-locale", Locale.class, Locale.GERMANY);

        // Register service
        bindService().to(SocietiesService.class);
        bindService().to(DictionaryService.class);

        // UUID provider
        bind(UUID.class).toProvider(TimeUUIDProvider.class);

        // Dictionary
        bindNamedInstance("dictionary-directory", File.class, new File(dataDirectory, "languages"));
        bind(new TypeLiteral<Dictionary<String>>() {}).to(new TypeLiteral<MutableDictionary<String>>() {});
        bind(new TypeLiteral<MutableDictionary<String>>() {}).to(new TypeLiteral<DefaultDictionary<String>>() {})
                .in(Singleton.class);


        // Database
        install(new DatabaseModule("localhost", "catharos", "root", "", 3306));

        // Commands
        install(new CommandModule());

        // Members
        install(new MemberModule());

        // Societies
        install(new SocietyModule());

        install(new ConfigModule(new JSONSource(new File(dataDirectory, "config.json"))));

        // Global stuff
        bind(Thread.UncaughtExceptionHandler.class).toInstance(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });

        // Player provider
        bind(PlayerProvider.class).to(BukkitPlayerProvider.class);


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

    private void prepareDefaults(File target) throws URISyntaxException, IOException {
        URL defaults = getClass().getClassLoader().getResource("defaults/");

        if (defaults != null) {
            File file = new File(defaults.toURI());
            FileUtils.copyDirectory(file, target);
        }
    }

}
