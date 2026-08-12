package xyz.pyxismc.serverLinksButton;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.ServerLinks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;

public final class ServerLinksButton extends JavaPlugin {

    private MiniMessage miniMessage;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        this.miniMessage = MiniMessage.miniMessage();

        registerServerLinks();

        getLogger().info("CustomMenuButton enabled.");

        int pluginId = 33315;
        Metrics metrics = new Metrics(this, pluginId);
    }

    private void registerServerLinks() {

        ServerLinks serverLinks =
                getServer().getServerLinks();

        ConfigurationSection links =
                getConfig().getConfigurationSection("links");

        if (links == null) {

            getLogger().warning(
                    "No 'links' section found in config.yml."
            );

            return;
        }

        for (String id : links.getKeys(false)) {

            ConfigurationSection link =
                    links.getConfigurationSection(id);

            if (link == null) {
                continue;
            }

            if (!link.getBoolean("enabled", true)) {
                continue;
            }

            String name =
                    link.getString("name");

            String urlString =
                    link.getString("url");

            if (name == null || name.isBlank()) {

                getLogger().warning(
                        "Link '" + id + "' has no name."
                );

                continue;
            }

            if (urlString == null || urlString.isBlank()) {

                getLogger().warning(
                        "Link '" + id + "' has no URL."
                );

                continue;
            }

            try {

                URI uri = URI.create(urlString);

                Component displayName =
                        miniMessage.deserialize(name);

                serverLinks.addLink(
                        displayName,
                        uri
                );

                getLogger().info(
                        "Registered link: " + id
                );

            } catch (IllegalArgumentException exception) {

                getLogger().warning(
                        "Invalid URL for link '" +
                                id +
                                "': " +
                                urlString
                );
            }
        }
    }
}