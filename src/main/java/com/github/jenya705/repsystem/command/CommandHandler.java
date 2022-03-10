package com.github.jenya705.repsystem.command;

import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.List;

/**
 * @author Jenya705
 */
public interface CommandHandler {

    void exec(CommandSender sender, Iterator<String> args);

    List<String> tab(CommandSender sender, Iterator<String> args);

}
