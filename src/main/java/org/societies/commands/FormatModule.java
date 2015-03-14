package org.societies.commands;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import net.catharos.lib.core.command.format.DefaultFormatter;
import net.catharos.lib.core.command.format.Formatter;
import net.catharos.lib.core.command.format.WidthProvider;
import net.catharos.lib.core.command.format.pagination.DefaultPaginator;
import net.catharos.lib.core.command.format.pagination.Paginator;
import net.catharos.lib.core.command.format.table.*;
import org.shank.AbstractModule;
import org.societies.MinecraftWidthProvider;
import org.societies.bridge.ChatColor;

/**
 * Represents a FormatModule
 */
public class FormatModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Table.class).to(FormattedTable.class);
        bindNamed("clean", Table.class).to(DefaultTable.class);
        bindNamedString("append", ChatColor.RESET.toString());

        install(new FactoryModuleBuilder()
                .implement(Row.class, Names.named("default"), DefaultRow.class)
                .implement(Row.class, Names.named("forward"), ForwardingRow.class)
                .implement(Row.class, Names.named("dictionary"), DictionaryRow.class)
                .build(RowFactory.class));

        bindNamedInstance("column-spacing", double.class, 24.0D);
        bindNamedInstance("max-line-length", double.class, 310.0D);

        bindNamed("default-color", String.class).toInstance(ChatColor.GRAY.toString());

        // Paginator
        bindNamedInstance("table-header", String.class, ChatColor.GRAY + "  Page {0} of {1}");
        bindNamedInstance("padding", int.class, 2);
        bind(Paginator.class).to(DefaultPaginator.class);

        bind(Formatter.class).to(DefaultFormatter.class);

        bind(WidthProvider.class).to(MinecraftWidthProvider.class);
    }
}
