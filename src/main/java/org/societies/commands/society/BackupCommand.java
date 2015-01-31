package org.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.uuid.UUIDStorage;
import org.apache.logging.log4j.Logger;
import org.shank.logging.InjectLogger;
import org.societies.database.json.GroupMapper;
import org.societies.database.json.MemberMapper;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.backup", async = true)
@Permission("societies.backup")
public class BackupCommand implements Executor<Sender> {

    @Argument(name = "argument.target.file")
    File output;

    private final GroupMapper groupMapper;
    private final MemberMapper memberMapper;
    private final MemberProvider memberProvider;
    private final GroupProvider groupProvider;

    @InjectLogger
    private Logger logger;

    @Inject
    public BackupCommand(GroupMapper groupMapper, MemberMapper memberMapper, MemberProvider memberProvider, GroupProvider groupProvider) {
        this.groupMapper = groupMapper;
        this.memberMapper = memberMapper;
        this.memberProvider = memberProvider;
        this.groupProvider = groupProvider;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, final Sender sender) throws ExecuteException {

        final UUIDStorage groupStorage = new UUIDStorage(new File(output, "groups"), "json");
        final UUIDStorage memberStorage = new UUIDStorage(new File(output, "members"), "json");

        final Set<Group> groups = groupProvider.getGroups();


        for (Group group : groups) {
            try {
                groupMapper.writeGroup(group, groupStorage.getFile(group.getUUID()));
            } catch (IOException e) {
                logger.catching(e);
            }
        }

        sender.send("backup.groups-finished");


        Set<Member> members = memberProvider.getMembers();

        for (Member member : members) {
            try {
                memberMapper.writeMember(member, memberStorage.getFile(member.getUUID()));
            } catch (IOException e) {
                logger.catching(e);
            }
        }

        sender.send("backup.members-finished");
    }
}
