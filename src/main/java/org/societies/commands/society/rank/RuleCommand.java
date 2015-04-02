package org.societies.commands.society.rank;

import com.google.inject.Inject;
import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.reflect.*;
import order.reflect.instance.Children;
import org.societies.commands.RuleStep;
import org.societies.commands.VerifyStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;

import java.util.Map;
import java.util.Set;

/**
 * Represents a RankCommand
 */
@Command(identifier = "command.rank.rules.rules")
@Children({
        RuleCommand.AssignCommand.class,
        RuleCommand.RemoveCommand.class,
        RuleCommand.ListCommand.class
})
@Sender(Member.class)
public class RuleCommand {

    //================================================================================
    // List
    //================================================================================

    @Command(identifier = "command.rank.rules.list")
    @Permission("societies.rank.rules.list")
    @Meta({@Entry(key = RuleStep.RULE, value = "rank.rules.list"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(Member.class)
    public static class ListCommand implements Executor<Member> {

        @Argument(name = "argument.rank", description = "The name of the new rank")
        String name;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Rank rank = group.getRank(name);

            if (rank == null) {
                sender.send("rank.not-found");
                return;
            }

            for (String r : rank.getAvailableRules()) {
                sender.send("rank.rules.list-format", r);
            }
        }

    }

    //================================================================================
    // Assign
    //================================================================================

    @Command(identifier = "command.rank.rules.assign")
    @Permission("societies.rank.rules.assign")
    @Meta({@Entry(key = RuleStep.RULE, value = "rank.rules.assign"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(Member.class)
    public static class AssignCommand implements Executor<Member> {

        @Argument(name = "argument.rank", description = "The name of the new rank")
        String name;


        @Argument(name = "argument.rank.rule")
        String rule;

        private final Set<String> rules;

        @Inject
        public AssignCommand(Set<String> rules) {
            this.rules = rules;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Rank rank = group.getRank(name);

            if (rank == null) {
                sender.send("rank.not-found");
                return;
            }

            if (!rules.contains(rule)) {
                return;
            }

            rank.addRule(rule);

            sender.send("rank.rules.assigned", rule, rank.getName());
        }
    }

    //================================================================================
    // Remove
    //================================================================================

    @Command(identifier = "command.rank.rules.remove")
    @Permission("societies.rank.rules.remove")
    @Meta({@Entry(key = RuleStep.RULE, value = "rank.rules.remove"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(Member.class)
    public static class RemoveCommand implements Executor<Member> {

        @Argument(name = "argument.rank", description = "The name of the new rank")
        String name;

        @Argument(name = "argument.rank.rule")
        String rule;

        private final Map<String, String> rules;

        @Inject
        public RemoveCommand(Map<String, String> rules) {
            this.rules = rules;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Rank rank = group.getRank(name);

            if (rank == null) {
                sender.send("rank.not-found");
                return;
            }


            String setting = rules.get(rule);

            if (setting == null) {
                return;
            }

            rank.removeRule(setting);

            sender.send("rank.rules.removed", rule, rank.getName());

        }
    }
}
