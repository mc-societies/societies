package net.catharos.societies.database.json;

import com.google.inject.Inject;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import net.catharos.groups.DefaultGroup;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.GroupGenerator;
import net.catharos.groups.rank.DefaultRank;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.groups.rank.StaticRank;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(JukitoRunner.class)
public class GroupMapperTest {

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(UUID.class).toProvider(TimeUUIDProvider.class);

            install(new FactoryModuleBuilder()
                    .implement(Group.class, DefaultGroup.class)
                    .build(GroupFactory.class));

            install(new FactoryModuleBuilder()
                    .implement(Rank.class, DefaultRank.class)
                    .implement(Rank.class, Names.named("static"), StaticRank.class)
                    .build(RankFactory.class));
        }
    }

    @Inject
    GroupMapper groupMapper;

    @Inject
    GroupGenerator generator;

    @Test
    public void testGroup() throws Exception {
        Group group = generator.generate();
        String data = groupMapper.writeGroup(group);
        Assert.assertEquals(group, groupMapper.readGroup(data));
    }
}
