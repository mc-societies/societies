package net.catharos.societies;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.QueryProvider;

/**
 * Represents a SocietiesQueries
 */
@Singleton
public class SocietiesQueries extends QueryProvider {

    @Inject
    protected SocietiesQueries(DSLProvider provider) {
        super(provider);
    }

    @Override
    public void build() {

    }
}
