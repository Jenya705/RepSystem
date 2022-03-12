package com.github.jenya705.repsystem.lp;

import com.github.jenya705.repsystem.RepSystem;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * @author Jenya705
 */
public class RepLPContext implements ContextCalculator<Player> {

    private final List<Map.Entry<String, Function<Integer, Boolean>>> functions;
    private final RepSystem plugin;

    public RepLPContext(RepSystem plugin) {
        this.plugin = plugin;
        functions = new CopyOnWriteArrayList<>();
        plugin.getConfig().getStringList("lp-context").forEach(s -> {
            Function<Integer, Boolean> func;
            if (s.startsWith(">")) {
                int val = Integer.parseInt(s.substring(1));
                func = it -> it > val;
            }
            else if (s.startsWith("<")) {
                int val = Integer.parseInt(s.substring(1));
                func = it -> it < val;
            }
            else {
                plugin.getLogger().warning("Failed to parse " + s);
                return;
            }
            functions.add(Maps.immutableEntry("player_reputation_" + s, func));
        });
    }

    @Override
    public void calculate(@NonNull Player target, @NonNull ContextConsumer consumer) {
        int reputation = plugin.getStorage().reputation(target.getUniqueId());
        consumer.accept("player_reputation", Integer.toString(reputation));
        functions.forEach(stringFunctionEntry -> consumer.accept(stringFunctionEntry.getKey(),
                Boolean.toString(stringFunctionEntry.getValue().apply(reputation))
        ));
    }

    @Override
    public @NonNull ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        functions.forEach(stringFunctionEntry -> builder
                .add(stringFunctionEntry.getKey(), "true")
                .add(stringFunctionEntry.getKey(), "false")
        );
        return builder.build();
    }
}
