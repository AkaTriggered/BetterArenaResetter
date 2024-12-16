package org.akatriggered.betterarenaresetter.commands;

import org.akatriggered.betterarenaresetter.ArenaCommand;
import org.akatriggered.betterarenaresetter.ArenaIO;
import org.akatriggered.betterarenaresetter.ConfigManager;
import org.akatriggered.betterarenaresetter.BetterArenaResetter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReloadCommand extends ArenaCommand {

    public ReloadCommand() {
        super("reload", "Reload all arenas", "/arena reload", new String[0]);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!sender.hasPermission("BetterArenaResetter.reload")) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " You don't have permission to run this command!");
            return;
        }

        ArenaIO.loadAllArenas();
        ConfigManager.setup();
        ResetCommand.ResetSpeed.reload();
        sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.GREEN + " Arenas and config reloaded!");
    }
}
