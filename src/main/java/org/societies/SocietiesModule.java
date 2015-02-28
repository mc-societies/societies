package org.societies;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.typesafe.config.*;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.shank.service.AbstractServiceModule;
import org.societies.api.NameProvider;
import org.societies.commands.CommandModule;
import org.societies.commands.FormatModule;
import org.societies.database.DatabaseModule;
import org.societies.dictionary.DictionaryModule;
import org.societies.group.SocietyModule;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.event.EventController;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.member.MemberModule;
import org.societies.member.locale.LocaleModule;
import org.societies.request.RequestModule;
import org.societies.setting.SettingModule;
import org.societies.sieging.SiegeModule;
import org.societies.teleport.TeleportModule;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

/**
 * Represents a SocietiesModule
 */
public class SocietiesModule extends AbstractServiceModule {

    private final File dataDirectory;
    private final Logger logger;
    public static final Key<ListeningExecutorService> WORKER_EXECUTOR = Key
            .get(ListeningExecutorService.class, Names.named("worker-executor"));

    public SocietiesModule(File dataDirectory, Logger logger) {
        this.dataDirectory = dataDirectory;
        this.logger = logger;
    }

    @Override
    protected void configure() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Error");
                e.printStackTrace(System.out);
            }
        });


//        binder().disableCircularProxies();

        ConfigParseOptions parseOptions = ConfigParseOptions.defaults()
                .setAllowMissing(false)
                .setSyntax(ConfigSyntax.CONF);

        Config defaultConfig = ConfigFactory
                .parseResources(SocietiesModule.class.getClassLoader(), "config.conf", parseOptions);

        File file = new File(dataDirectory, "config.conf");

        Config config;
        if (file.exists()) {
            config = ConfigFactory.parseFile(file, parseOptions).withFallback(defaultConfig);
        } else {
            config = ConfigFactory.empty().withFallback(defaultConfig);
        }


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
            logger.catching(e);
        }

        bind(Config.class).toInstance(config);

        install(new ConfigModule(config));

        install(new SettingModule());

        // Logging
        bindNamed("service-logger", Logger.class).toInstance(logger);

        bind(Logger.class).toInstance(logger);

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
        install(new SocietyModule(config));

        // Dictionary
        install(new DictionaryModule(dataDirectory));

        // Global stuff
        bind(Thread.UncaughtExceptionHandler.class).toInstance(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.catching(e);
            }
        });

        // Executor service for heavy work
        bind(WORKER_EXECUTOR).toInstance(MoreExecutors.listeningDecorator(Executors.newCachedThreadPool()));

        bind(ListeningExecutorService.class).to(WORKER_EXECUTOR);

        // Chat rendering
        install(new FormatModule());


        install(new TeleportModule(config.getBoolean("teleport.enabled")));

        install(new RequestModule());

        bindNamed("fallback", NameProvider.class).to(MojangNameProvider.class);

        try {
            bindNamedInstance("translations-url", new URL(config.getString("translations-url")));
        } catch (MalformedURLException e) {
            logger.catching(e);
        }

        bind(EventController.class).to(DefaultEventController.class);


        newSetBinder(binder(), new TypeLiteral<ExtensionRoller<Member>>() {});
        newSetBinder(binder(), new TypeLiteral<ExtensionRoller<Group>>() {});

        install(new SiegeModule(config, dataDirectory));
    }

    @Singleton
    @Provides
    public DateTimeFormatter provideDateTimeFormatter(@Named("default-locale") Locale locale) {
        return DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("MM", locale));
    }

    @Singleton
    @Provides
    public PeriodFormatter providePeriodFormatter(Dictionary<String> dictionary) {
        return new PeriodFormatterBuilder()
                .appendYears()
                .appendSuffix(" " + dictionary.getTranslation("year"), " " + dictionary.getTranslation("years"))
                .appendSeparator(" ")
                .appendMonths()
                .appendSuffix(" " + dictionary.getTranslation("month"), " " + dictionary.getTranslation("months"))
                .appendSeparator(" ")
                .appendDays()
                .appendSuffix(" " + dictionary.getTranslation("day"), " " + dictionary.getTranslation("days"))
                .appendSeparator(" ")
                .appendMinutes()
                .appendSuffix(" " + dictionary.getTranslation("minute"), " " + dictionary.getTranslation("minutes"))
                .appendSeparator(" ")
                .appendSeconds()
                .appendSuffix(" " + dictionary.getTranslation("second"), " " + dictionary.getTranslation("seconds"))
                .toFormatter();
    }

}
