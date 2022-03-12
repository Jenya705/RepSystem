package com.github.jenya705.repsystem.command;

import com.github.jenya705.repsystem.RepSystem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class PlayerRepCommand implements CommandHandler {

    public final RepSystem plugin;

    @Override
    public void exec(CommandSender sender, Iterator<String> args) {
        UUID player;
        String nickname;
        if (args.hasNext()) {
            nickname = args.next();
            player = plugin.getStorage().getUUID(nickname);
            if (player == null) {
                sender.sendMessage(plugin.getMessage().getPlayerNotOnline());
                return;
            }
        }
        else if (sender instanceof Player playerObject){
            player = playerObject.getUniqueId();
            nickname = playerObject.getName();
        }
        else {
            sender.sendMessage("Provide player nickname");
            return;
        }
        sender.sendMessage(plugin.getMessage().getPlayerRep(
                nickname,
                plugin.getStorage().reputation(player)
        ));
    }

    @Override
    public List<String> tab(CommandSender sender, Iterator<String> args) {
        if (!args.hasNext()) {
            return Collections.emptyList();
        }
        return null;
    }
}
