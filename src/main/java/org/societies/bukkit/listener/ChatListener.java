package org.societies.bukkit.listener;

import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.shank.config.ConfigSetting;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;

/**
 * Represents a ChatListener
 */
class ChatListener implements Listener {

    private final boolean integration;
    private final MemberProvider provider;

    @Inject
    public ChatListener(@ConfigSetting("chat.integration") boolean integration, MemberProvider provider) {
        this.integration = integration;
        this.provider = provider;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!integration) {
            return;
        }

        Member member = provider.getMember(event.getPlayer().getUniqueId());

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
