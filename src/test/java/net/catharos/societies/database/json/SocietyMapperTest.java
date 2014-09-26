package net.catharos.societies.database.json;

import com.google.inject.Inject;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.DefaultGroup;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(JukitoRunner.class)
public class SocietyMapperTest {

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(UUID.class).toProvider(TimeUUIDProvider.class);
            install(new FactoryModuleBuilder()
                    .implement(Group.class, DefaultGroup.class)
                    .build(GroupFactory.class));
        }
    }

    @Inject
    SocietyMapper mapper;
    private String data = "{" +
            "\"uuid\": \"d58be4c0-459b-11e4-916c-0800200c9a66\"," +
            "\"name\": \"name\"," +
            "\"tag\": \"tag\"," +
            "\"ranks\": [ \"d58be4c0-459b-11e4-916c-0800200c9a66\" ]" +
            "}";

    @Test
    public void testReadGroup() throws Exception {

        Group read = mapper.read(data);

    }
}
