package com.github.jenya705.repsystem.papi;

import com.github.jenya705.repsystem.RepSystem;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class RepPapiExpansion extends PlaceholderExpansion {

    private static final String playerReputation = "player_reputation";
    private static final String coloredPlayerReputation = "colored_player_reputation";

    private final RepSystem plugin;

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return "jenya705";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return plugin.isEnabled();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        System.out.println(params + " " + player);
        boolean colored = params.startsWith(coloredPlayerReputation);
        if (colored || params.startsWith(playerReputation)) {
            String playerArg = colored ?
                    params.substring(coloredPlayerReputation.length()) :
                    params.substring(playerReputation.length());
            int reputation;
            if (playerArg.length() <= 1) {
                if (player == null) return null;
                reputation = plugin.getStorage().reputation(player.getUniqueId());
            }
            else {
                String playerNickname = playerArg.substring(1);
                UUID uuid = Bukkit.getPlayerUniqueId(playerNickname);
                reputation = plugin.getStorage().reputation(uuid);
            }
            return colored ? plugin.getMessage().color(reputation) : "" + reputation;
        }
        return null;
    }
}
