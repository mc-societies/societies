package org.societies.bukkit.listener;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;

/**
 * Represents a JoinListener
 */
class JoinListener implements Listener {

    private final MemberProvider memberProvider;
    private final ListeningExecutorService service;

    private final Logger logger;

    @Inject
    public JoinListener(MemberProvider memberProvider, ListeningExecutorService service, Logger logger) {
        this.memberProvider = memberProvider;
        this.service = service;
        this.logger = logger;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerLoginEvent event) {
        ListenableFuture<?> future = service.submit(new Runnable() {
            @Override
            public void run() {
                Member member = memberProvider.getMember(event.getPlayer().getUniqueId());
                member.activate();
            }
        });

        Futures.addCallback(future, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.catching(throwable);
            }
        });
    }
}
