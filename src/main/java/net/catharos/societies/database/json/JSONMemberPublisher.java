package net.catharos.societies.database.json;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.*;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.shank.logging.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

/**
 * Represents a JSONMemberPublisher
 */
public final class JSONMemberPublisher implements
        MemberCreatedPublisher, MemberLastActivePublisher,
        MemberGroupPublisher, MemberRankPublisher, SettingPublisher {

    private final MemberPublisher memberPublisher;

    @InjectLogger
    private Logger logger;

    @Inject
    public JSONMemberPublisher(MemberPublisher memberPublisher) {
        this.memberPublisher = memberPublisher;
    }

    @Override
    public <V> void publish(Subject subject, Target target, Setting<V> setting, @Nullable V value) {
        memberPublisher.publish((Member) subject);
    }

    @Override
    public ListenableFuture<Member> publishCreated(Member member, DateTime created) {
        return memberPublisher.publish(member);
    }

    @Override
    public ListenableFuture<Member> publishGroup(Member member, Group group) {
        return memberPublisher.publish(member);
    }

    @Override
    public ListenableFuture<Member> publishLastActive(Member member, DateTime date) {
        return memberPublisher.publish(member);
    }

    @Override
    public ListenableFuture<Member> publishRank(Member member, Rank rank) {
        return memberPublisher.publish(member);
    }

    @Override
    public ListenableFuture<Member> dropRank(Member member, Rank rank) {
        return memberPublisher.publish(member);
    }
}
