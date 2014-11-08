package net.catharos.societies;

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.lib.core.i18n.MutableDictionary;
import net.catharos.lib.core.i18n.StringDictionary;
import net.catharos.lib.shank.AbstractModule;

import java.io.File;

/**
 * Represents a DictionaryModule
 */
public class DictionaryModule extends AbstractModule {

    private final File dataDirectory;

    public DictionaryModule(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    protected void configure() {
        bindNamedInstance("dictionary-directory", File.class, new File(dataDirectory, "translations"));

        Key<MutableDictionary<String>> dictionaryKey = Key.get(new TypeLiteral<MutableDictionary<String>>() {});
        bind(dictionaryKey).to(new TypeLiteral<StringDictionary>() {})
                .in(Singleton.class);
        bind(new TypeLiteral<Dictionary<String>>() {}).to(dictionaryKey);
    }
}
