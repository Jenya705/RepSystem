package com.github.jenya705.repsystem.command;

import com.github.jenya705.repsystem.RepSystem;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
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
        String nickname = args.next();
        Player player = Bukkit.getPlayer(nickname);
        UUID uuid = plugin.getStorage().getUUID(nickname);
        if (uuid == null) {
            sender.sendMessage(plugin.getMessage().getPlayerNotOnline());
            return;
        }
        if (Objects.equals(player, sender)) {
            sender.sendMessage(plugin.getMessage().getCanNotRepYourself());
            return;
        }
        int reputation = 1;
        boolean isAdmin = false;
        if (args.hasNext()) {
            if (!sender.hasPermission("rep." + operation + ".more")) {
                sender.sendMessage(plugin.getMessage().getNoPerms());
                return;
            }
            try {
                reputation = Integer.parseUnsignedInt(args.next());
                isAdmin = true;
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage().getNan());
                return;
            }
        }
        if (isAdmin) {
            int resultReputation = plugin.getStorage().reputation(uuid) + reputation * coefficient;
            plugin.getStorage().setReputation(uuid, resultReputation);
        }
        else if (sender instanceof Player who) {
            plugin.getStorage().giveReputation(uuid, who.getUniqueId(), coefficient > 0);
        }
        else {
            sender.sendMessage("Only for players!");
            return;
        }
        int resultReputation = plugin.getStorage().reputation(uuid);
        sender.sendMessage(reputatorMessage.apply(nickname, resultReputation));
        if (reputation == 1 && player != null) player.sendMessage(getterMessage.apply(sender.getName(), resultReputation));
    }

    @Override
    public List<String> tab(CommandSender sender, Iterator<String> args) {
        if (args.hasNext()) {
            return null;
        }
        return Collections.emptyList();
    }
}
