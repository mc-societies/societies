package org.societies.commands;

import order.Translate;
import order.format.DefaultFormatter;
import order.format.Formatter;
import order.format.WidthProvider;
import order.format.pagination.DefaultPaginator;
import order.format.pagination.Paginator;
import order.format.table.*;
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

        bind(RowFactory.class).to(DefaultRowFactory.class);

        bindNamedInstance("column-spacing", double.class, 24.0D);
        bindNamedInstance("max-line-length", double.class, 310.0D);

        bindNamed("default-color", String.class).toInstance(ChatColor.GRAY.toString());

        // Paginator
        bindNamedInstance("table-header", String.class, ChatColor.GRAY + "  Page {0} of {1}");
        bindNamedInstance("padding", int.class, 2);
        bind(Paginator.class).to(DefaultPaginator.class);

        bind(Formatter.class).to(DefaultFormatter.class);

        bind(WidthProvider.class).to(MinecraftWidthProvider.class);

        bind(Translate.class).to(DictionaryTranslate.class);
    }
}
