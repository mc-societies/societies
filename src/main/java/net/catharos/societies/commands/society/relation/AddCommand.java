package net.catharos.societies.commands.society.relation;

import net.catharos.groups.Member;
import net.catharos.groups.Relation;
import net.catharos.lib.core.command.Executor;

/**
* Represents a CreateCommand
*/
abstract class AddCommand implements Executor<Member> {



    protected abstract String getSuccessMessage();

    protected abstract Relation.Type getType();
}
