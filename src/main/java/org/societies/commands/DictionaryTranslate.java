package org.societies.commands;

import com.google.inject.Inject;
import order.Translate;
import org.societies.groups.dictionary.Dictionary;

/**
 * Represents a DictionaryTranslate
 */
public class DictionaryTranslate implements Translate {

    private final Dictionary<String> dictionary;

    @Inject
    public DictionaryTranslate(Dictionary<String> dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public String getTranslation(String name) {
        return dictionary.getCleanTranslation(name);
    }


}
