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
import net.catharos.societies.member.MemberModule;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        install(new DatabaseModule("localhost", "societies", "root", "", 3306));

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
        extract("defaults", target);
    }

    private void extract(String path, File target) throws IOException, URISyntaxException {
        File jarFile = new File(SocietiesModule.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        int offset = path.length() + 1;

        if (jarFile.isFile()) {
            JarFile jar = new JarFile(jarFile);
            Enumeration enumEntries = jar.entries();
            while (enumEntries.hasMoreElements()) {
                JarEntry entry = (JarEntry) enumEntries.nextElement();

                String name = entry.getName();
                if (!name.startsWith(path + "/") || name.length() <= offset) {
                    continue;
                }


                File file = new File(target, name.substring(offset));
                if (entry.isDirectory()) { // if its a directory, create it
                    file.mkdirs();
                    continue;
                } else {
                    file.createNewFile();
                }

                InputStream is = jar.getInputStream(entry); // get the input stream
                FileOutputStream fos = new FileOutputStream(file);
                while (is.available() > 0) {  // write contents of 'is' to 'fos'
                    fos.write(is.read());
                }
                fos.close();
                is.close();
            }
            jar.close();
        } else {
            URL url = SocietiesModule.class.getResource("/" + path);
            if (url != null) {
                File file = new File(url.toURI());

                FileUtils.copyDirectory(file, target);
            }
        }
    }
}
