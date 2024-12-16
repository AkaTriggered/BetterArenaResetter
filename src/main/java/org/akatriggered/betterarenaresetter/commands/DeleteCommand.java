package org.akatriggered.betterarenaresetter.commands;

import org.akatriggered.betterarenaresetter.Arena;
import org.akatriggered.betterarenaresetter.ArenaCommand;
import org.akatriggered.betterarenaresetter.BetterArenaResetter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends ArenaCommand {

    public DeleteCommand() {
        super("remove", "Delete an arena", "/arena remove <arena>", new String[] {"delete"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!sender.hasPermission("BetterArenaResetter.remove")) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " You don't have permission to run this command!");
            return;
        }

        if (!BetterArenaResetter.INSTANCE.isReady()) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " Arenas have not finished loading yet!");
            return;
        }

        if (args.size() == 0) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " You must provide an arena name! Usage is /arena remove <arena>");
            return;
        }

        if (!Arena.arenas.containsKey(args.get(0).toLowerCase())) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " Arena not found! Use /arena list");
            return;
        }

        File file = new File(BetterArenaResetter.INSTANCE.getDataFolder(), "Arenas/" + args.get(0).toLowerCase() + ".dat");

        if (!file.exists()) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " How odd! That arena no longer exists! I'll clean up the dregs so this won't happen again.");
            Arena.arenas.remove(args.get(0).toLowerCase());
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.GREEN + " Done! Arena removed. Goodbye!");
            return;
        }
        sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.YELLOW + " Are you sure you want to do this? This cannot be undone!");
        sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.YELLOW + " To confirm this, use " + ChatColor.RED + "/arena confirm");

        Runnable confirm = new Runnable() {
            @Override
            public void run() {
                file.delete();
                Arena.arenas.remove(args.get(0).toLowerCase());
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.GREEN + " Arena removed! Goodbye forever!");
                return;
            }
        };

        ConfirmCommand.confirmTasks.put(sender, confirm);

        new BukkitRunnable() {

            @Override
            public void run() {
                ConfirmCommand.confirmTasks.remove(sender);
            }
        }.runTaskLater(BetterArenaResetter.INSTANCE, 20 * 30);

    }

    @Override
    protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
        List<String> completions = new ArrayList<>();
        if (args.size() < 2) {
            completions.addAll(Arena.arenas.keySet());
        }
        return completions;
    }
}
