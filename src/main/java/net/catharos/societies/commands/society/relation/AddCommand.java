package net.catharos.societies.commands.society.relation;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.Relation;
import net.catharos.groups.RelationFactory;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;

/**
* Represents a CreateCommand
*/
abstract class AddCommand implements Executor<Member> {

    @Argument(name = "argument.target.society")
    Group target;

    private final RelationFactory factory;

    public AddCommand(RelationFactory factory) {this.factory = factory;}

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Relation relation = factory.create(group, target, getType());

        group.setRelation(target, relation);

        sender.send(getSuccessMessage(), target.getName());
    }

    protected abstract String getSuccessMessage();

    protected abstract Relation.Type getType();
}
