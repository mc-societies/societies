package net.catharos.societies.commands.society.relation;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.Relation;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.api.member.SocietyMember;
import org.apache.logging.log4j.Logger;

import javax.inject.Provider;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Represents a ListCommand
 */
abstract class ListCommand implements Executor<Member> {

    private final Provider<Table> tableProvider;
    private final MemberProvider<SocietyMember> memberProvider;

    @Option(name = "argument.page")
    int page;

    @InjectLogger
    private Logger logger;

    public ListCommand(Provider<Table> tableProvider, MemberProvider<SocietyMember> memberProvider) {
        this.tableProvider = tableProvider;
        this.memberProvider = memberProvider;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Collection<Relation> relations = group.getRelations();

        if (relations.isEmpty()) {
            sender.send("relations.not-found");
            return;
        }

        Table table = tableProvider.get();

        for (Relation relation : relations) {
            Member member;
            try {
                member = memberProvider.getMember(relation.getOpposite(group.getUUID())).get();
            } catch (InterruptedException e) {
                logger.catching(e);
                continue;
            } catch (ExecutionException e) {
                logger.catching(e);
                continue;
            }

            table.addRow(member);
        }

        sender.send(table.render(ctx.getName(), page));
    }

    protected abstract Relation.Type getType();
}
