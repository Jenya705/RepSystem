package com.github.jenya705.repsystem.command;

import com.github.jenya705.repsystem.RepSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Jenya705
 */
public class RootRepCommand implements TabExecutor {

    private final Map<String, CommandHandler> commands;
    private final List<String> tab;

    public RootRepCommand(RepSystem plugin) {
        commands = new HashMap<>();
        commands.put("+", new PlusRepCommand(plugin));
        commands.put("-", new MinusRepCommand(plugin));
        commands.put("see", new PlayerRepCommand(plugin));
        tab = new ArrayList<>(commands.keySet());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Iterator<String> iteratorArgs = Arrays.asList(args).iterator();
        if (iteratorArgs.hasNext()) {
            CommandHandler handler = commands.get(iteratorArgs.next().toLowerCase(Locale.ROOT));
            if (handler == null) return true;
            handler.exec(sender, iteratorArgs);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        Iterator<String> iteratorArgs = Arrays.asList(args).iterator();
        if (iteratorArgs.hasNext()) {
            CommandHandler handler = commands.get(iteratorArgs.next());
            if (handler == null) return tab;
            return handler.tab(sender, iteratorArgs);
        }
        return tab;
    }
}
