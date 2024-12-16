package org.akatriggered.betterarenaresetter.commands;

import org.akatriggered.betterarenaresetter.Arena;
import org.akatriggered.betterarenaresetter.ArenaCommand;
import org.akatriggered.betterarenaresetter.BetterArenaResetter;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateCommand extends ArenaCommand {

    public CreateCommand() {
        super("create", "Create a new arena", "/arena create", new String[] {});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " This command must be run by players!");
            return;
        }

        if (!sender.hasPermission("BetterArenaResetter.create")) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " You don't have permission to run this command!");
            return;
        }

        String wand = BetterArenaResetter.INSTANCE.getRegionSelection().getWand().toString();

        if (args.size() == 0) {
            if (BetterArenaResetter.INSTANCE.getRegionSelection().hasSelectedRegion((Player)sender)) {
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.GREEN + " You've already selected a region. Finish the arena creation with /arena create [name]");
            } else {
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " Create an arena by selecting a region with a " + wand + " and then run /arena create [name]");
            }
        } else {
            if (Arena.arenas.containsKey(args.get(0).toLowerCase())) {
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " An arena by that name already exists!");
                return;
            }

            if (!BetterArenaResetter.INSTANCE.getRegionSelection().hasSelectedRegion((Player)sender)) {
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " Create an arena by selecting a region with a " + wand + " and then run /arena create [name]");
                return;
            }

            if (!args.get(0).matches("[\\w\\d]{3,}") || args.size() > 1) {
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " The arena name must be made of alpha-numeric characters and at least 3 letters long!");
                return;
            }

            Block[] corners = BetterArenaResetter.INSTANCE.getRegionSelection().getRegionCorners((Player)sender);
            Arena.createNewArena(args.get(0).toLowerCase(), corners[0].getLocation(), corners[1].getLocation(), (Player)sender);

        }
    }
}
