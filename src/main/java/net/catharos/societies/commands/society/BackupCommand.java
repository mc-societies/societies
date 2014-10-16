package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.Member;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.uuid.UUIDStorage;
import net.catharos.societies.database.json.GroupMapper;
import net.catharos.societies.database.json.MemberMapper;
import net.catharos.societies.member.SocietyMember;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.backup")
@Permission("societies.backup")
public class BackupCommand implements Executor<Sender> {

    @Argument(name = "argument.target.file")
    File output;

    private final GroupMapper groupMapper;
    private final MemberMapper<?> memberMapper;
    private final MemberProvider<?> memberProvider;
    private final GroupProvider groupProvider;

    @Inject
    public BackupCommand(GroupMapper groupMapper, MemberMapper<SocietyMember> memberMapper, MemberProvider<SocietyMember> memberProvider, GroupProvider groupProvider) {
        this.groupMapper = groupMapper;
        this.memberMapper = memberMapper;
        this.memberProvider = memberProvider;
        this.groupProvider = groupProvider;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {

        final UUIDStorage groupStorage = new UUIDStorage(new File(output, "groups"), "json");
        final UUIDStorage memberStorage = new UUIDStorage(new File(output, "members"), "json");

        final ListenableFuture<Set<Group>> groups = groupProvider.getGroups();

        Futures.addCallback(groups, new FutureCallback<Set<Group>>() {
            @Override
            public void onSuccess(Set<Group> result) {
                for (Group group : result) {
                    try {
                        groupMapper.writeGroup(group, groupStorage.getFile(group.getUUID()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });


        ListenableFuture<? extends Set<? extends Member>> members = memberProvider.getMembers();

        Futures.addCallback(members, new FutureCallback<Set<? extends Member>>() {
            @Override
            public void onSuccess(Set<? extends Member> result) {
                for (Member member : result) {
                    try {
                        memberMapper.writeMember(member, memberStorage.getFile(member.getUUID()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
