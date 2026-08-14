package xyz.pyxismc.serverLinksButton.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.DialogKeys;
import io.papermc.paper.registry.keys.tags.DialogTagKeys;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import xyz.pyxismc.serverLinksButton.common.ServerLinksConfig;

import java.util.List;

public final class LinksBootstrap implements PluginBootstrap {

    private static final Key SERVER_LINKS =
            Key.key(
                    "serverlinksbutton",
                    "server_links"
            );

    @Override
    public void bootstrap(BootstrapContext context) {

        ServerLinksConfig config =
                ServerLinksConfig.load(
                        context.getDataDirectory(),
                        context.getLogger()::warn
                );

        context.getLifecycleManager()
                .registerEventHandler(
                        RegistryEvents.DIALOG.compose()
                                .newHandler(event -> {

                                    event.registry().register(
                                            DialogKeys.create(
                                                    SERVER_LINKS
                                            ),

                                            builder -> builder
                                                    .base(
                                                            DialogBase.builder(
                                                                            config.title()
                                                                    )
                                                                    .externalTitle(
                                                                            config.externalTitle()
                                                                    )
                                                                    .canCloseWithEscape(
                                                                            config.canCloseWithEscape()
                                                                    )
                                                                    .build()
                                                    )
                                                    .type(
                                                            DialogType.serverLinks(
                                                                    null,
                                                                    config.columns(),
                                                                    config.buttonWidth()
                                                            )
                                                    )
                                    );
                                })
                );

        context.getLifecycleManager()
                .registerEventHandler(
                        LifecycleEvents.TAGS
                                .preFlatten(
                                        RegistryKey.DIALOG
                                )
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
}
