package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import net.catharos.groups.Group;
import net.catharos.groups.request.*;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.member.SocietyMember;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a InviteCommand
 */
@Command(identifier = "command.invite")
public class InviteCommand implements Executor<SocietyMember> {

    @Argument(name = "argument.target.member")
    SocietyMember target;

    public static final String FAILED = "Invite failed! %s";

    private final Dictionary<String> dictionary;

    public InviteCommand(Dictionary<String> dictionary) {this.dictionary = dictionary;}

    @Override
    public void execute(CommandContext<SocietyMember> ctx, final SocietyMember sender) {
        String name = dictionary.getTranslation("requests.invite", sender.getName(), sender.getGroup().getName());
        SimpleRequest request = new SimpleRequest(name, sender, new SimpleRequestMessenger<Choices>(), new SingleInvolved(target));
        request.start();

        addCallback(request.result(), new FutureCallback<SimpleRequestResult<Choices>>() {
            @Override
            public void onSuccess(@Nullable SimpleRequestResult<Choices> result) {
                if (result == null) {
                    return;
                }

                switch (result.getChoice()) {
                    case ACCEPT:
                        Group group = sender.getGroup();
                        group.addMember(target);
                        target.send("You've been added to %s!", group.getName());
                        sender.send(target.getName() + " is not a member of your society!");
                        break;
                    case DENY:
                    case ABSTAIN:
                        target.send(FAILED);
                        sender.send(FAILED);
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
