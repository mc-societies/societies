package net.catharos.societies.commands;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import net.catharos.bridge.ChatColor;
import net.catharos.lib.core.command.format.DefaultFormatter;
import net.catharos.lib.core.command.format.Formatter;
import net.catharos.lib.core.command.format.WidthProvider;
import net.catharos.lib.core.command.format.table.*;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.MinecraftWidthProvider;

/**
 * Represents a FormatModule
 */
public class FormatModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Table.class).to(FormattedTable.class);
        bindNamed("clean", Table.class).to(DefaultTable.class);
        bindNamedString("append", ChatColor.RESET.toString());
        bind(WidthProvider.class).toInstance(new MinecraftWidthProvider());

        install(new FactoryModuleBuilder()
                .implement(Row.class, Names.named("default"), DefaultRow.class)
                .implement(Row.class, Names.named("forward"), ForwardingRow.class)
                .implement(Row.class, Names.named("dictionary"), DictionaryRow.class)
                .build(RowFactory.class));

        bindNamedInstance("column-spacing", double.class, 12.0D);
        bindNamedInstance("max-line-length", double.class, 315.0D);
        bind(Formatter.class).to(DefaultFormatter.class);
    }
}
