package Unbound.unboundAPI.chat;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Pattern;

public final class ChatUtil {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('§')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    private static final Pattern RGB_CODE = Pattern.compile("(?i)\u00a7x(?:\u00a7[0-9a-f]){6}");
    private static final Pattern LEGACY_CODE = Pattern.compile("(?i)\u00a7[0-9a-fk-or]");

    private ChatUtil() {
    }
}
