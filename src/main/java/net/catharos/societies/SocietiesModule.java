package net.catharos.societies;

import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import net.catharos.lib.database.Database;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.database.DatabaseModule;
import net.catharos.societies.group.SocietyModule;
import net.catharos.societies.member.MemberModule;

import java.util.UUID;

/**
 * Represents a SocietiesModule
 */
public class SocietiesModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        bindService().to(SocietiesService.class);

        bind(UUID.class).toProvider(TimeUUIDProvider.class);
        bind(Dictionary.class);

        install(new DatabaseModule("localhost", "catharos", "root", "", Database.DEFAULT_PORT));
        bind(SocietiesQueries.class);

        install(new SocietyModule());
        install(new MemberModule());
    }
}
