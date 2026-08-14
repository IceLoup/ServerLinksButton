package xyz.pyxismc.serverLinksButton.paper;

import org.bukkit.ServerLinks;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.pyxismc.serverLinksButton.common.ServerLinksConfig;

public final class ServerLinksButton extends JavaPlugin {

    @Override
    public void onEnable() {

        new org.bstats.bukkit.Metrics(
                this,
                33315
        );

        ServerLinksConfig config =
                ServerLinksConfig.load(
                        getDataFolder().toPath(),
                        getLogger()::warning
                );

        registerServerLinks(config);

        getLogger().info(
                "ServerLinksButton enabled on Paper."
        );
    }

    private void registerServerLinks(
            ServerLinksConfig config
    ) {

        ServerLinks serverLinks =
                getServer().getServerLinks();

        for (ServerLinksConfig.Link link : config.links()) {

            try {

                serverLinks.addLink(
                        link.name(),
                        link.url()
                );

                getLogger().info(
                        "Registered server link: " + link.id()
                );

            } catch (IllegalArgumentException exception) {

                getLogger().warning(
                        "Invalid URL for link '" +
                                link.id() +
                                "': " +
                                link.url()
                );
            }
        }
    }
}
