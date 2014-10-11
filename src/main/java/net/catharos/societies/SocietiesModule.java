package net.catharos.societies;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.typesafe.config.*;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import net.catharos.lib.shank.config.ConfigModule;
import net.catharos.lib.shank.config.TypeSafeConfigSource;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.bukkit.BukkitNameProvider;
import net.catharos.societies.bukkit.BukkitPlayerProvider;
import net.catharos.societies.commands.CommandModule;
import net.catharos.societies.commands.FormatModule;
import net.catharos.societies.database.DatabaseModule;
import net.catharos.societies.group.SocietyModule;
import net.catharos.societies.launcher.ReloadAction;
import net.catharos.societies.launcher.SocietiesPlugin;
import net.catharos.societies.member.MemberModule;
import net.catharos.societies.member.locale.LocaleModule;
import net.catharos.societies.request.RequestModule;
import net.catharos.societies.setting.SettingModule;
import net.catharos.societies.teleport.TeleportModule;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * Represents a SocietiesModule
 */
public class SocietiesModule extends AbstractServiceModule {

    private final File dataDirectory;
    public static final Key<ListeningExecutorService> WORKER_EXECUTOR = Key
            .get(ListeningExecutorService.class, Names.named("worker-executor"));

    public SocietiesModule(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    protected void configure() {
//        binder().disableCircularProxies();

        ConfigParseOptions parseOptions = ConfigParseOptions.defaults()
                .setSyntax(ConfigSyntax.CONF);

        Config defaultConfig = ConfigFactory
                .parseResources(SocietiesModule.class.getClassLoader(), "config.conf", parseOptions);

        File file = new File(dataDirectory, "config.conf");

        Config config = ConfigFactory.parseFile(file, parseOptions).withFallback(defaultConfig);

        ConfigRenderOptions renderOptions = ConfigRenderOptions
                .defaults()
                .setOriginComments(false)
                .setJson(false)
                .setFormatted(true);

        String rendered = config.root().render(renderOptions);


        config = config.resolve();

        try {
            FileUtils.writeStringToFile(file, rendered);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bind(Config.class).toInstance(config);

        install(new ConfigModule(new TypeSafeConfigSource(config)));

        install(new SettingModule());

        // Logging
        bindNamed("service-logger", Logger.class).toInstance(LogManager.getLogger());

        // Register service
        bindService().to(SocietiesService.class);
        bindService().to(DictionaryService.class);

        // UUID provider
        bind(UUID.class).toProvider(TimeUUIDProvider.class);

        // Database
        install(new DatabaseModule(config, dataDirectory));

        // Commands
        install(new CommandModule());

        // Members
        install(new MemberModule());
        install(new LocaleModule(LocaleUtils.toLocale(config.getString("language"))));

        // Societies
        install(new SocietyModule());


        // Dictionary
        install(new DictionaryModule(dataDirectory));

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
        bind(WORKER_EXECUTOR).toInstance(MoreExecutors.listeningDecorator(Executors.newCachedThreadPool()));

        bind(ListeningExecutorService.class).to(WORKER_EXECUTOR);

        // Chat rendering
        install(new FormatModule());

        bind(NameProvider.class).to(BukkitNameProvider.class);

        bind(ReloadAction.class).to(SocietiesPlugin.class);

        install(new TeleportModule());

        install(new RequestModule());

        try {
            bindNamedInstance("translations-url", new URL(config.getString("translations-url")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
