package net.catharos.societies.commands;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import net.catharos.lib.core.command.format.DefaultFormatter;
import net.catharos.lib.core.command.format.Formatter;
import net.catharos.lib.core.command.format.MonospacedWidthProvider;
import net.catharos.lib.core.command.format.WidthProvider;
import net.catharos.lib.core.command.format.table.*;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a FormatModule
 */
public class FormatModule extends AbstractModule {

    @Override
    protected void configure() {
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