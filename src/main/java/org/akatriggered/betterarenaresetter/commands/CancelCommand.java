package org.akatriggered.betterarenaresetter.commands;

import org.akatriggered.betterarenaresetter.Arena;
import org.akatriggered.betterarenaresetter.ArenaCommand;
import org.akatriggered.betterarenaresetter.BetterArenaResetter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CancelCommand extends ArenaCommand {

    public CancelCommand() {
        super("cancel", "Cancel an arena reset", "/arena cancel [arena]", new String[0]);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!sender.hasPermission("BetterArenaResetter.reset")) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " You don't have permission to run this command!");
            return;
        }

        if (args.size() > 0) {
            if (!Arena.arenas.containsKey(args.get(0).toLowerCase())) {
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " Arena by that name not found!");
                return;
            }

            if (!Arena.arenas.get(args.get(0).toLowerCase()).isBeingReset()) {
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " That arena is not currently being reset!");
                return;
            }

            Arena.arenas.get(args.get(0).toLowerCase()).cancelReset();
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.YELLOW + " Reset for arena \"" + args.get(0).toLowerCase() + "\" canceled!");
        } else {
            //If they have a pending confirm task, cancel it
            if (ConfirmCommand.confirmTasks.containsKey(sender)) {
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.YELLOW + " Task cancelled.");
                ConfirmCommand.confirmTasks.remove(sender);
                return;
            }

            for (Arena arena : Arena.arenas.values()) {
                if (arena.isBeingReset()) {
                    arena.cancelReset();
                    sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.YELLOW + " Reset for arena \"" + arena.getName() + "\" canceled!");
                    return;
                }
            }

            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " No arenas are currently being reset!");
            return;
        }
    }

    @Override
    protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
        if (args.size() <= 1) return Arena.arenas.values().stream().filter(Arena::isBeingReset).map(Arena::getName).collect(Collectors.toList());

        return new ArrayList<>();
    }
}
