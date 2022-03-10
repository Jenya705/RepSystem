package com.github.jenya705.repsystem.command;

import com.github.jenya705.repsystem.RepSystem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class PlayerRepCommand implements CommandHandler {

    public final RepSystem plugin;

    @Override
    public void exec(CommandSender sender, Iterator<String> args) {
        Player player;
        if (args.hasNext()) {
            player = Bukkit.getPlayer(args.next());
            if (player == null) {
                sender.sendMessage(plugin.getMessage().getPlayerNotOnline());
                return;
            }
        }
        else if (sender instanceof Player){
            player = (Player) sender;
        }
        else {
            sender.sendMessage("Provide player nickname");
            return;
        }
        sender.sendMessage(plugin.getMessage().getPlayerRep(
                player.getName(),
                plugin.getStorage().reputation(player.getUniqueId())
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
