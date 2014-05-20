package net.catharos.societies.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.catharos.groups.DefaultGroup;
import net.catharos.groups.Group;
import net.catharos.societies.SocietiesModule;
import net.catharos.societies.SocietiesQueries;

/**
 * Represents a SocietiesMain
 */
public class SocietiesMain {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new SocietiesModule());

        Group group = injector.getInstance(DefaultGroup.class);

        injector.getInstance(SocietiesQueries.class);
    }
}
