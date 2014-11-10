package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.member.SocietyMember;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.ExecutionException;

/**
 * Represents a ChatListener
 */
public class ChatListener implements Listener {

    private final MemberProvider<SocietyMember> provider;

    @InjectLogger
    private Logger logger;

    @Inject
    public ChatListener(MemberProvider<SocietyMember> provider) {this.provider = provider;}

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        SocietyMember member;
        try {
            member = provider.getMember(event.getPlayer().getUniqueId()).get();
        } catch (InterruptedException e) {
            logger.catching(e);
            return;
        } catch (ExecutionException e) {
            logger.catching(e);
            return;
        }

        String format = event.getFormat();
        Group group = member.getGroup();

        String tag = group == null ? "" : group.getTag();
        String name = group == null ? "" : group.getName();
        String uuid = group == null ? "" : group.getUUID().toString();

        format = format.replace("{group-tag}", tag);
        format = format.replace("{group-name}", name);
        format = format.replace("{group-uuid}", uuid);

        event.setFormat(format);
    }
}
