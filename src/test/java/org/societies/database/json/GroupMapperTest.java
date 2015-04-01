package org.societies.database.json;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import gnu.trove.set.hash.THashSet;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.group.RandomGroupGenerator;
import org.societies.groups.group.memory.MemoryGroupFactory;
import org.societies.groups.rank.DefaultRank;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;
import org.societies.groups.rank.StaticRank;

import java.util.Set;
import java.util.UUID;

@RunWith(JukitoRunner.class)
@Ignore //todo remove ignore
public class GroupMapperTest {

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(UUID.class).toProvider(TimeUUIDProvider.class);

            bind(GroupFactory.class).to(MemoryGroupFactory.class);

            install(new FactoryModuleBuilder()
                    .implement(Rank.class, DefaultRank.class)
                    .implement(Rank.class, Names.named("static"), StaticRank.class)
                    .build(RankFactory.class));

            bindNamed(new TypeLiteral<Set<Rank>>() {
            }, "predefined-ranks").toInstance(new THashSet<Rank>());
        }
    }

    @Inject
    GroupMapper groupMapper;

    @Inject
    RandomGroupGenerator generator;

    @Test
    public void testGroup() throws Exception {
        Group group = generator.generate();
        String data = groupMapper.writeGroup(group);
        Assert.assertEquals(group, groupMapper.readGroup(data));
    }
}
