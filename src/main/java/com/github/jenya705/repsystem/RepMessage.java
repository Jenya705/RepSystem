package com.github.jenya705.repsystem;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class RepMessage {

    private final RepSystem plugin;

    public Component getPlusRep(String playerNickname, int reputation) {
        return message("plus-rep", args(playerNickname, reputation));
    }

    public Component getOtherPlusRep(String playerNickname, int reputation) {
        return message("other-plus-rep", args(playerNickname, reputation));
    }

    public Component getMinusRep(String playerNickname, int reputation) {
        return message("minus-rep", args(playerNickname, reputation));
    }

    public Component getOtherMinusRep(String playerNickname, int reputation) {
        return message("other-minus-rep", args(playerNickname, reputation));
    }

    public Component getPlayerRep(String playerNickname, int reputation) {
        return message("player-rep", args(playerNickname, reputation));
    }

    public Component getNoPerms() {
        return message("no-perms");
    }

    public Component getCanNotRepYourself() {
        return message("cant-rep-yourself");
    }

    public Component getPlayerNotOnline() {
        return message("player-not-online");
    }

    public Component getNan() {
        return message("nan");
    }

    public Component getDelay() {
        return message("delay");
    }

    private String[] args(String nickname, int reputation) {
        return new String[]{
                "%player_nickname%",
                nickname,
                "%reputation%",
                color(reputation) + reputation
        };
    }

    private String color(int value) {
        return value < 0 ?
                plugin.getConfig().getString("messages.neg-color", "&c") :
                value > 0 ?
                        plugin.getConfig().getString("messages.pos-color", "&a") :
                        plugin.getConfig().getString("messages.zero-color", "&7");
    }

    private Component message(String key, String... placeholders) {
        String endMessage = plugin.getConfig().getString("messages." + key, "failed to load");
        for (int i = 0; i < placeholders.length;) {
            endMessage = endMessage.replace(placeholders[i++], placeholders[i++]);
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(endMessage);
    }

}
