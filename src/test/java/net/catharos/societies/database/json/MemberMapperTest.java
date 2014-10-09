package net.catharos.societies.database.json;

import com.google.inject.Inject;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.DefaultGroup;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.GroupGenerator;
import net.catharos.groups.rank.DefaultRank;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.lib.core.uuid.TimeUUIDProvider;
import net.catharos.societies.member.SocietyMember;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(JukitoRunner.class)
public class MemberMapperTest {

    public static class Module extends JukitoModule {
        @Override
        protected void configureTest() {
            bind(UUID.class).toProvider(TimeUUIDProvider.class);

            install(new FactoryModuleBuilder()
                    .implement(Group.class, DefaultGroup.class)
                    .build(GroupFactory.class));

            install(new FactoryModuleBuilder()
                    .implement(Rank.class, DefaultRank.class)
                    .build(RankFactory.class));
        }
    }

    @Inject
    MemberMapper<SocietyMember> mapper;

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
