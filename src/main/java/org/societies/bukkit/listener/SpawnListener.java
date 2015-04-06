package org.societies.bukkit.listener;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.shank.config.ConfigSetting;
import org.societies.api.math.Location;
import org.societies.api.group.Society;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;


/**
 * Represents a SpawnListener
 */
class SpawnListener implements Listener {

    private final boolean respawnHome;
    private final MemberProvider memberProvider;
    private final ListeningExecutorService service;

    private final Logger logger;

    @Inject
    public SpawnListener(@ConfigSetting("home.replace-spawn") boolean respawnHome,
                         MemberProvider memberProvider,
                         ListeningExecutorService service, Logger logger) {

        this.respawnHome = respawnHome;
        this.memberProvider = memberProvider;
        this.service = service;
        this.logger = logger;
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        if (respawnHome) {
            final Player player = event.getPlayer();


            ListenableFuture<?> future = service.submit(new Runnable() {
                @Override
                public void run() {
                    Member result = memberProvider.getMember(player.getUniqueId());

                    Group group = result.getGroup();
                    if (group == null) {
                        return;
                    }

                    Society society = group.get(Society.class);

                    Optional<Location> location = society.getHome();


                    if (location.isPresent()) {
                        result.get(Player.class).teleport(location.get().toBukkit());
                    }
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
}
