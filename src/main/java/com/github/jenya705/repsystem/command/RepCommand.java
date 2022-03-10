package com.github.jenya705.repsystem.command;

import com.github.jenya705.repsystem.RepSystem;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class RepCommand implements CommandHandler {

    private final RepSystem plugin;
    private final String operation;
    private final int coefficient;
    private final BiFunction<String, Integer, Component> reputatorMessage;
    private final BiFunction<String, Integer, Component> getterMessage;

    @Override
    public void exec(CommandSender sender, Iterator<String> args) {
        if (!args.hasNext()) return;
        Player player = Bukkit.getPlayer(args.next());
        if (player == null) {
            sender.sendMessage(plugin.getMessage().getPlayerNotOnline());
            return;
        }
        if (player.equals(sender)) {
            sender.sendMessage(plugin.getMessage().getCanNotRepYourself());
            return;
        }
        int reputation = 1;
        boolean skipDelay = false;
        if (args.hasNext()) {
            if (!sender.hasPermission("rep." + operation + ".more")) {
                sender.sendMessage(plugin.getMessage().getNoPerms());
                return;
            }
            try {
                reputation = Integer.parseUnsignedInt(args.next());
                skipDelay = true;
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage().getNan());
                return;
            }
        }
        if (!skipDelay && sender instanceof Player playerSender) {
            if (!plugin.getStorage().checkDelay(player.getUniqueId(), playerSender.getUniqueId())) {
                sender.sendMessage(plugin.getMessage().getDelay());
                return;
            }
            plugin.getStorage().setDelay(player.getUniqueId(), playerSender.getUniqueId());
        }
        int resultReputation = plugin.getStorage().reputation(player.getUniqueId()) + reputation * coefficient;
        plugin.getStorage().setReputation(player.getUniqueId(), resultReputation);
        sender.sendMessage(reputatorMessage.apply(player.getName(), resultReputation));
        if (reputation == 1) player.sendMessage(getterMessage.apply(player.getName(), resultReputation));
    }

    @Override
    public List<String> tab(CommandSender sender, Iterator<String> args) {
        if (args.hasNext()) {
            return null;
        }
        return Collections.emptyList();
    }
}
