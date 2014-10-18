package net.catharos.societies.commands;

import com.google.inject.Inject;
import net.catharos.groups.Member;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.sender.Sender;

import java.util.Map;

/**
 * Represents a RuleStep
 */
public class RuleStep implements Executor<Sender> {

    public static final String RULE = "rule";

    private final Map<String, Setting> rules;

    @Inject
    public RuleStep(Map<String, Setting> rules) {this.rules = rules;}

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        if (!(sender instanceof Member)) {
            return;
        }

        Member member = ((Member) sender);

        String rule = ctx.getCommand().get(RULE);

        if (rule == null) {
            return;
        }

        Setting setting = rules.get(rule);

        if (setting == null) {
            ctx.cancel();
            throw new ExecuteException("No setting for rule " + rule + " found!", ctx);
        }

        Object result = member.get(setting);

        if (result == null) {
            sender.send("no-rule");
            ctx.cancel();
        }
    }
}
