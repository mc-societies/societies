package net.catharos.societies.commands.society.relation;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.Relation;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;

/**
* Represents a RemoveCommand
*/
abstract class RemoveCommand implements Executor<Member> {

    @Argument(name = "argument.target.society")
    Group target;

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        group.removeRelation(target);

        sender.send(getSuccessMessage(), target.getName());
    }

    protected abstract String getSuccessMessage();

    protected abstract Relation.Type getType();
}
