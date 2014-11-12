package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Member;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.ff")
@Permission("societies.ff")
@Sender(Member.class)
public class FFCommand implements Executor<Member> {

    private final Setting<Boolean> personalFF;

    @Inject
    public FFCommand(@Named("personal-friendly-fire") Setting<Boolean> personalFF) {
        this.personalFF = personalFF;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        boolean ff = sender.getBoolean(personalFF);
        sender.set(personalFF, !ff);
        sender.send("personal-ff.toggled", !ff ? "ff.allow" : "ff.auto");
    }
}
