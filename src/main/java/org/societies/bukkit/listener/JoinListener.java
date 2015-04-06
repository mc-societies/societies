package org.societies.bukkit.listener;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;

/**
 * Represents a JoinListener
 */
class JoinListener implements Listener {

    private final MemberProvider memberProvider;
    private final ListeningExecutorService service;

    @Inject
    public JoinListener(MemberProvider memberProvider, ListeningExecutorService service) {
        this.memberProvider = memberProvider;
        this.service = service;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerLoginEvent event) {
        service.submit(new Activator(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        service.submit(new Activator(event.getPlayer()));
    }

    private class Activator implements Runnable {
        private final Player player;

        public Activator(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            Member member = memberProvider.getMember(player.getUniqueId());

            member.activate();
        }
    }
}
