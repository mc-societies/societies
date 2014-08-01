package net.catharos.societies;

import com.google.common.util.concurrent.ListeningExecutorService;
import net.catharos.lib.core.util.JarUtils;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import net.catharos.lib.shank.config.ConfigModule;
import net.catharos.lib.shank.config.JSONSource;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.bukkit.BukkitPlayerProvider;
import net.catharos.societies.commands.CommandModule;
import net.catharos.societies.commands.FormatModule;
import net.catharos.societies.database.DatabaseModule;
import net.catharos.societies.database.sql.SQLModule;
import net.catharos.societies.group.SocietyModule;
import net.catharos.societies.member.MemberModule;
import net.catharos.societies.member.locale.LocaleModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;

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
        binder().disableCircularProxies();
        // Configuration
        try {
            JarUtils.extract("defaults", dataDirectory);
        } catch (URISyntaxException e) {
           throw new RuntimeException("Failed to create a URL of the code's source!", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to prepare resources!");
        }

        // Logging
        bindNamed("service-logger", Logger.class).toInstance(LogManager.getLogger());

        // Register service
        bindService().to(SocietiesService.class);
        bindService().to(DictionaryService.class);

        // UUID provider
        bind(UUID.class).toProvider(TimeUUIDProvider.class);

        // Database
        install(new DatabaseModule());

        // Commands
        install(new CommandModule());

        // Members
        install(new MemberModule());
        install(new LocaleModule());

        // Societies
        install(new SocietyModule());

        install(new SQLModule());

        // Dictionary
        install(new DictionaryModule(dataDirectory));

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
        bind(ListeningExecutorService.class).toInstance(sameThreadExecutor(/*newFixedThreadPool(2)*/));

        // Chat rendering
        install(new FormatModule());
    }

}
