package xyz.pyxismc.serverLinksButton;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DialogKeys;
import io.papermc.paper.registry.keys.tags.DialogTagKeys;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class LinksBootstrap implements PluginBootstrap {

    private static final Key SERVER_LINKS =
            Key.key(
                    "custommenubutton",
                    "server_links"
            );

    private static final MiniMessage MINI_MESSAGE =
            MiniMessage.miniMessage();

    @Override
    public void bootstrap(BootstrapContext context) {

        YamlConfiguration config = loadConfig(context);


        String externalTitleString =
                config.getString(
                        "dialog.external-title",
                        "Sah"
                );

        String titleString =
                config.getString(
                        "dialog.title",
                        "Sah"
                );

        boolean canCloseWithEscape =
                config.getBoolean(
                        "dialog.can-close-with-escape",
                        true
                );

        final int columns = Math.max(
                1,
                config.getInt("dialog.columns", 1)
        );

        final int buttonWidth = Math.max(
                1,
                Math.min(
                        1024,
                        config.getInt("dialog.button-width", 310)
                )
        );

        context.getLifecycleManager().registerEventHandler(
                RegistryEvents.DIALOG.compose()
                        .newHandler(event -> {

                            event.registry().register(
                                    DialogKeys.create(
                                            SERVER_LINKS
                                    ),

                                    builder -> builder

                                            .base(
                                                    DialogBase.builder(
                                                                    deserialize(
                                                                            titleString
                                                                    )
                                                            )

                                                            .externalTitle(
                                                                    deserialize(
                                                                            externalTitleString
                                                                    )
                                                            )

                                                            .canCloseWithEscape(
                                                                    canCloseWithEscape
                                                            )

                                                            .build()
                                            )

                                            .type(
                                                    DialogType.serverLinks(
                                                            null,
                                                            columns,
                                                            buttonWidth
                                                    )
                                            )
                            );
                        })
        );

        context.getLifecycleManager().registerEventHandler(
                io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents.TAGS
                        .preFlatten(RegistryKey.DIALOG)
                        .newHandler(event -> {

                            event.registrar().addToTag(
                                    DialogTagKeys.PAUSE_SCREEN_ADDITIONS,

                                    List.of(
                                            TagEntry.valueEntry(
                                                    DialogKeys.create(
                                                            SERVER_LINKS
                                                    )
                                            )
                                    )
                            );
                        })
        );
    }

    private static Component deserialize(String text) {

        if (text == null || text.isBlank()) {
            return Component.empty();
        }

        return MINI_MESSAGE.deserialize(text);
    }

    private static YamlConfiguration loadConfig(
            BootstrapContext context
    ) {

        Path dataDirectory =
                context.getDataDirectory();

        Path configFile =
                dataDirectory.resolve("config.yml");

        try {

            Files.createDirectories(dataDirectory);

        } catch (IOException exception) {

            context.getLogger().error(
                    "Could not create plugin data directory.",
                    exception
            );
        }

        if (!Files.exists(configFile)) {

            try {

                try (InputStream resource =
                        LinksBootstrap.class
                                .getClassLoader()
                                .getResourceAsStream("config.yml")) {

                    if (resource != null) {

                        Files.copy(
                                resource,
                                configFile
                        );

                    }

                }

            } catch (IOException exception) {

                context.getLogger().error(
                        "Could not create default config.yml.",
                        exception
                );
            }
        }

        if (Files.exists(configFile)) {

            return YamlConfiguration.loadConfiguration(
                    configFile.toFile()
            );
        }

        context.getLogger().warn(
                "config.yml could not be loaded. "
                        + "Using fallback values."
        );

        return new YamlConfiguration();
    }
}
