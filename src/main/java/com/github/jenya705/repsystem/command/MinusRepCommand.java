package com.github.jenya705.repsystem.command;

import com.github.jenya705.repsystem.RepSystem;

/**
 * @author Jenya705
 */
public class MinusRepCommand extends RepCommand {

    public MinusRepCommand(RepSystem plugin) {
        super(plugin, "-", -1,
                plugin.getMessage()::getMinusRep,
                plugin.getMessage()::getOtherMinusRep
        );
    }
}
