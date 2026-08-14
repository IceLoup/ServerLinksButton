package xyz.pyxismc.serverLinksButton.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.ServerLink;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.velocity.Metrics;
import xyz.pyxismc.serverLinksButton.common.ServerLinksConfig;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Plugin(
        id = "serverlinksbutton",
        name = "ServerLinksButton",
        version = "1.0.0",
        description = "Customizes the Server Links button.",
        authors = {"IceLoup"},
        url = "https://pyxismc.xyz"
)
public final class ServerLinksVelocity {

    private final ProxyServer proxy;

    private final Path dataDirectory;

    private final org.slf4j.Logger logger;

    private final Metrics.Factory metricsFactory;

    private final MiniMessage miniMessage =
            MiniMessage.miniMessage();

    private List<ServerLink> links =
            List.of();

    @Inject
    public ServerLinksVelocity(
            ProxyServer proxy,
            @DataDirectory Path dataDirectory,
            org.slf4j.Logger logger,
            Metrics.Factory metricsFactory
    ) {

        this.proxy = proxy;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization(
            ProxyInitializeEvent event
    ) {

        ServerLinksConfig config =
                ServerLinksConfig.load(
                        dataDirectory,
                        logger::warn
                );

        links = new ArrayList<>();

        for (ServerLinksConfig.Link link : config.links()) {

            links.add(
                    ServerLink.serverLink(
                            link.name(),
                            link.url().toString()
                    )
            );
        }

        metricsFactory.make(
                this,
                33315
        );

        proxy.getConsoleCommandSource()
                .sendMessage(
                        miniMessage.deserialize(
                                "<green>ServerLinksButton "
                                        + "enabled on Velocity."
                        )
                );
    }

    @Subscribe
    public void onPostLogin(
            PostLoginEvent event
    ) {

        Player player =
                event.getPlayer();

        /*
         * Velocity requires Minecraft 1.21+
         * for setServerLinks().
         */
        try {

            player.setServerLinks(
                    List.copyOf(links)
            );

        } catch (IllegalArgumentException exception) {

            logger.warn(
                    "Could not send server links to "
                            + player.getUsername()
            );
        }
    }
}
