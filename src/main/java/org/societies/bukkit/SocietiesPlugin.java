package org.societies.bukkit;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.typesafe.config.*;
import net.milkbowl.vault.economy.Economy;
import order.*;
import order.sender.Sender;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.shank.service.ServiceController;
import org.shank.service.ServiceModule;
import org.shank.service.lifecycle.Lifecycle;
import org.societies.SocietiesModule;
import org.societies.bukkit.economy.DummyEconomy;
import org.societies.bukkit.logging.LoggerWrapper;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents a SocietiesPlugin
 */
public class SocietiesPlugin extends JavaPlugin implements Listener {

    private Injector injector;

    private Commands<Sender> commands;
    private MemberProvider memberProvider;
    private ServiceController serviceController;
    private Sender systemSender;
    private Logger logger;

    private ListeningExecutorService service;


    @Override
    public void onEnable() {
        logger = new LoggerWrapper(this.getLogger());

        Economy economy;

        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager()
                .getRegistration(Economy.class);

        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        } else {
            economy = new DummyEconomy();
            logger.info("You need to install Vault to use the economy features");
        }

        File dir = this.getDataFolder();

        logger.info("Reloading AK-47... Please wait patiently!");

        Config config = loadConfig(dir);

        injector = Guice.createInjector(
                new ServiceModule(),
                new SocietiesModule(dir, logger, config),
                new BukkitModule(this.getServer(), this, economy)
        );

        logger.info("Well done.");

        serviceController = injector.getInstance(ServiceController.class);

        serviceController.invoke(Lifecycle.INITIALISING);


        this.getServer().getPluginManager().registerEvents(this, this);
        commands = injector.getInstance(Key.get(new TypeLiteral<Commands<Sender>>() {}));
        memberProvider = injector.getInstance(Key.get(new TypeLiteral<MemberProvider>() {}));
        systemSender = injector.getInstance(Key.get(Sender.class, Names.named("system-sender")));
        service = injector.getInstance(SocietiesModule.WORKER_EXECUTOR);

        serviceController.invoke(Lifecycle.STARTING);
    }

    private Config loadConfig(File dir) {
        ConfigParseOptions parseOptions = ConfigParseOptions.defaults()
                .setAllowMissing(false)
                .setSyntax(ConfigSyntax.CONF);

        Config defaultConfig = ConfigFactory
                .parseResources(SocietiesModule.class.getClassLoader(), "config.conf", parseOptions);

        File file = new File(dir, "config.conf");

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

        return config;
    }

    void print() throws UnsupportedEncodingException {
        try {
            printMarkdownPermissions(new PrintStream(new FileOutputStream("permissions"), true, "UTF-8"));
            printMarkdownCommands(new PrintStream(new FileOutputStream("commands"), true, "UTF-8"));
            printPluginYMLPermissions(new PrintStream(new FileOutputStream("this"), true, "UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void printPluginYMLPermissions(final PrintStream stream) {
        commands.iterate(new FormatCommandIterator<Sender>("/", " - ", " [?]") {
            @Override
            public void iterate(order.Command<Sender> command, String format) {
                if (command.getPermission() == null) {
                    return;
                }
                stream.println("  " + command.getPermission() + ":");
                stream.println("    description: " + "\"Allows you to use the command \'" + format + "\'\"");
            }
        }, true);
    }

    public void printMarkdownPermissions(final PrintStream stream) {
        commands.iterate(new FormatCommandIterator<Sender>("/", " - ", " [?]") {
            @Override
            public void iterate(order.Command<Sender> command, String format) {
                if (command.getPermission() == null) {
                    return;
                }
                stream.println("|" + command.getPermission() + "|" + format + "|");
            }
        }, true);
    }

    public void printMarkdownCommands(final PrintStream stream) {
        commands.iterate(new FormatCommandIterator<Sender>("/", " - ", false, " [?]") {
            @Override
            public void iterate(order.Command<Sender> command, String format) {
                if (command.getPermission() == null) {
                    return;
                }
                stream.println("|" + format + "|" + command.getDescription() + "|");
            }
        }, true);
    }

    @Override
    public void onDisable() {
        if (injector == null) {
            return;
        }

        service.shutdown();

        try {

            service.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // Nobody fucking cares!
            logger.catching(e);
        }

        serviceController.invoke(Lifecycle.STOPPING);

        logger.info("Engines and weapons unloaded and locked!");
    }


    @Override
    public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command command, String label, final String[] args) {

        if (injector == null) {
            sender.sendMessage("Societies failed to start somehow, sorry :/ Fuck the dev!!");
            return true;
        }

        if (sender instanceof Player) {

//            ListenableFuture<?> future = service.submit(new Runnable() {
//                @Override
//                public void run() {
//                    Member member = memberProvider.getMember(((Player) sender).getUniqueId());
//                    commands.execute(member, command.getName(), args);
//
//                }
//            });

            Member member = memberProvider.getMember(((Player) sender).getUniqueId());
            commands.execute(member, command.getName(), args);

        } else {
            commands.execute(systemSender, command.getName(), args);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, final org.bukkit.command.Command command, String alias, final String[] args) {
        if (sender instanceof Player) {

            if (args.length < 1) {
                return null;
            }

            String[] arguments = new String[args.length - 1];
            System.arraycopy(args, 0, arguments, 0, args.length - 1);

            // Using dummy sender
            CommandContext<Sender> ctx = commands.createContext(new SystemSender(), command.getName(), arguments);

            order.Command<Sender> groupCommand = ctx.getCommand();

            if (groupCommand instanceof GroupCommand) {

                List<String> output = new ArrayList<String>(((GroupCommand<Sender>) groupCommand).size());

                for (order.Command<Sender> cmd : ((GroupCommand<Sender>) groupCommand).getChildren()) {
                    if (cmd.getIdentifier().startsWith(args[args.length - 1])) {
                        output.add(cmd.getIdentifier());
                    }
                }

                return output;
            }
        }

        return null;
    }

    public ClassLoader getPluginClassLoader() {
        return getClassLoader();
    }
}
