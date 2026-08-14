package xyz.pyxismc.serverLinksButton.common;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ServerLinksConfig {

    private static final MiniMessage MINI_MESSAGE =
            MiniMessage.miniMessage();

    private final Map<String, Object> data;

    private ServerLinksConfig(Map<String, Object> data) {
        this.data = data;
    }

    public static ServerLinksConfig load(
            Path dataDirectory,
            Consumer<String> warn
    ) {

        Path configFile =
                dataDirectory.resolve("config.yml");

        try {

            Files.createDirectories(dataDirectory);

        } catch (IOException exception) {

            warn.accept(
                    "Could not create plugin directory."
            );
        }

        if (!Files.exists(configFile)) {
            copyDefaultConfig(
                    configFile,
                    warn
            );
        }

        if (Files.exists(configFile)) {

            Map<String, Object> parsed =
                    parse(configFile);

            if (parsed != null) {
                return new ServerLinksConfig(parsed);
            }

            warn.accept(
                    "Could not parse config.yml, "
                            + "falling back to defaults."
            );
        }

        return new ServerLinksConfig(
                parseDefaults()
        );
    }

    public Component title() {
        return deserialize(
                getString(
                        "dialog.title",
                        "Server Links"
                )
        );
    }

    public Component externalTitle() {
        return deserialize(
                getString(
                        "dialog.external-title",
                        "Server Links"
                )
        );
    }

    public boolean canCloseWithEscape() {
        return getBoolean(
                "dialog.can-close-with-escape",
                true
        );
    }

    public int columns() {
        return Math.max(
                1,
                getInt(
                        "dialog.columns",
                        1
                )
        );
    }

    public int buttonWidth() {
        return Math.max(
                1,
                Math.min(
                        1024,
                        getInt(
                                "dialog.button-width",
                                310
                        )
                )
        );
    }

    public List<Link> links() {

        List<Link> result =
                new ArrayList<>();

        Object linksValue =
                get("links");

        if (!(linksValue instanceof Map<?, ?> linksMap)) {
            return result;
        }

        for (Map.Entry<?, ?> entry : linksMap.entrySet()) {

            Object value =
                    entry.getValue();

            if (!(value instanceof Map<?, ?>)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> link =
                    (Map<String, Object>) value;

            String id =
                    String.valueOf(entry.getKey());

            if (!getBoolean(
                    link,
                    "enabled",
                    true
            )) {
                continue;
            }

            String name =
                    getString(
                            link,
                            "name",
                            null
                    );

            String url =
                    getString(
                            link,
                            "url",
                            null
                    );

            if (name == null || name.isBlank()) {
                continue;
            }

            if (url == null || url.isBlank()) {
                continue;
            }

            try {

                result.add(
                        new Link(
                                id,
                                deserialize(name),
                                URI.create(url)
                        )
                );

            } catch (IllegalArgumentException ignored) {
                // Invalid URL
            }
        }

        return result;
    }

    private static void copyDefaultConfig(
            Path configFile,
            Consumer<String> warn
    ) {

        try (InputStream resource =
                     ServerLinksConfig.class
                             .getClassLoader()
                             .getResourceAsStream(
                                     "config.yml"
                             )) {

            if (resource != null) {

                Files.copy(
                        resource,
                        configFile
                );
            }

        } catch (IOException exception) {

            warn.accept(
                    "Could not create default config.yml."
            );
        }
    }

    private static Map<String, Object> parse(
            Path configFile
    ) {

        try (InputStream input =
                     Files.newInputStream(configFile)) {

            return parse(input);

        } catch (IOException exception) {
            return null;
        }
    }

    private static Map<String, Object> parseDefaults() {

        try (InputStream resource =
                     ServerLinksConfig.class
                             .getClassLoader()
                             .getResourceAsStream(
                                     "config.yml"
                             )) {

            if (resource != null) {

                Map<String, Object> parsed =
                        parse(resource);

                if (parsed != null) {
                    return parsed;
                }
            }

        } catch (IOException ignored) {
            // Empty config
        }

        return new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parse(
            InputStream input
    ) {

        Object loaded =
                new Yaml().load(
                        new InputStreamReader(
                                input,
                                StandardCharsets.UTF_8
                        )
                );

        if (loaded instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }

        return null;
    }

    private Object get(String path) {

        Object current =
                data;

        for (String key : path.split("\\.")) {

            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }

            current =
                    map.get(key);
        }

        return current;
    }

    private String getString(
            String path,
            String defaultValue
    ) {

        Object value =
                get(path);

        return value instanceof String string
                ? string
                : defaultValue;
    }

    private boolean getBoolean(
            String path,
            boolean defaultValue
    ) {

        Object value =
                get(path);

        return value instanceof Boolean bool
                ? bool
                : defaultValue;
    }

    private int getInt(
            String path,
            int defaultValue
    ) {

        Object value =
                get(path);

        return value instanceof Number number
                ? number.intValue()
                : defaultValue;
    }

    private static String getString(
            Map<String, Object> map,
            String key,
            String defaultValue
    ) {

        Object value =
                map.get(key);

        return value instanceof String string
                ? string
                : defaultValue;
    }

    private static boolean getBoolean(
            Map<String, Object> map,
            String key,
            boolean defaultValue
    ) {

        Object value =
                map.get(key);

        return value instanceof Boolean bool
                ? bool
                : defaultValue;
    }

    private static Component deserialize(
            String text
    ) {

        if (text == null || text.isBlank()) {
            return Component.empty();
        }

        return MINI_MESSAGE.deserialize(text);
    }

    public record Link(
            String id,
            Component name,
            URI url
    ) {
    }
}
