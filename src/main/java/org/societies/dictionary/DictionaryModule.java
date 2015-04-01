package org.societies.dictionary;

import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.lib.core.i18n.MutableDictionary;
import org.shank.service.AbstractServiceModule;
import org.societies.StringDictionary;

import java.io.File;

/**
 * Represents a DictionaryModule
 */
public class DictionaryModule extends AbstractServiceModule {

    private final File dataDirectory;

    public DictionaryModule(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    protected void configure() {
        // Register service
        bindService().to(DictionaryService.class);

        bindNamedInstance("dictionary-directory", File.class, new File(dataDirectory, "translations"));

        Key<MutableDictionary<String>> dictionaryKey = Key.get(new TypeLiteral<MutableDictionary<String>>() {});
        bind(dictionaryKey).to(new TypeLiteral<StringDictionary>() {})
                .in(Singleton.class);
        bind(new TypeLiteral<Dictionary<String>>() {}).to(dictionaryKey);
    }
}
