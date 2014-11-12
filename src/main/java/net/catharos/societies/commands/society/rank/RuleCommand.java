package net.catharos.societies.commands.society.rank;

import com.google.common.collect.Table;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.commands.VerifyStep;
import net.catharos.societies.setting.RulesSetting;

import java.util.Map;

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

        @Argument(name = "argument.rank.name", description = "The name of the new rank")
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

            for (Table.Cell<Setting, Target, Object> cell : rank.getSettings().cellSet()) {
                if (cell.getRowKey() instanceof RulesSetting) {
                    RulesSetting ruleSetting = (RulesSetting) cell.getRowKey();
                    sender.send("rank.rules.list-format", ruleSetting.getRule());
                }
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

        @Argument(name = "argument.rank.name", description = "The name of the new rank")
        String name;


        @Argument(name = "argument.rank.rule")
        String rule;

        private final Map<String, Setting> rules;

        @Inject
        public AssignCommand(Map<String, Setting> rules) {this.rules = rules;}

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

            Setting setting = rules.get(rule);

            if (setting == null) {
                return;
            }

            rank.set(setting, true);

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

        @Argument(name = "argument.rank.name", description = "The name of the new rank")
        String name;

        @Argument(name = "argument.rank.rule")
        String rule;

        private final Map<String, Setting> rules;

        @Inject
        public RemoveCommand(Map<String, Setting> rules) {this.rules = rules;}

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


            Setting setting = rules.get(rule);

            if (setting == null) {
                return;
            }

            rank.remove(setting);

            sender.send("rank.rules.removed", rule, rank.getName());

        }
    }
}
