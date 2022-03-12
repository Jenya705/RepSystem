package com.github.jenya705.repsystem;

import com.github.jenya705.repsystem.command.RootRepCommand;
import com.github.jenya705.repsystem.lp.RepLPContext;
import com.github.jenya705.repsystem.papi.RepPapiExpansion;
import com.github.jenya705.repsystem.storage.RepStorage;
import lombok.Getter;
import lombok.SneakyThrows;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

@Getter
public final class RepSystem extends JavaPlugin {

    private final RepMessage message = new RepMessage(this);

    private RepStorage storage;
    private Duration delay;

    @Override
    @SneakyThrows
    public void onEnable() {
        saveDefaultConfig();
        delay = getDuration("delay");
        storage = new RepStorage(this);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RepPapiExpansion(this).register();
        }
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            LuckPerms luckPerms = LuckPermsProvider.get();
            luckPerms.getContextManager().registerCalculator(new RepLPContext(this));
        }
        getCommand("rep").setExecutor(new RootRepCommand(this));
    }

    @Override
    @SneakyThrows
    public void onDisable() {
        if (storage != null) {
            storage.save();
        }
    }

    public Duration getDuration(String key) {
        String duration = getConfig().getString(key);
        if (duration == null) return null;
        int number = Integer.parseInt(duration.substring(0, duration.length() - 1));
        if (duration.endsWith("d")) {
            return Duration.ofDays(number);
        }
        else if (duration.endsWith("h")) {
            return Duration.ofHours(number);
        }
        else if (duration.endsWith("m")) {
            return Duration.ofMinutes(number);
        }
        else if (duration.endsWith("s")) {
            return Duration.ofSeconds(number);
        }
        return null;
    }
}
