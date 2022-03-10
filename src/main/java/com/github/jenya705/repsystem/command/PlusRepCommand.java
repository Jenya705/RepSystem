package com.github.jenya705.repsystem.command;

import com.github.jenya705.repsystem.RepSystem;

/**
 * @author Jenya705
 */
public class PlusRepCommand extends RepCommand {

    public PlusRepCommand(RepSystem plugin) {
        super(plugin, "+", 1,
                plugin.getMessage()::getPlusRep,
                plugin.getMessage()::getOtherPlusRep
        );
    }
}
