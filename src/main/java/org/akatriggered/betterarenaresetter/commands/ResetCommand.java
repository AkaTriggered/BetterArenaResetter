package org.akatriggered.betterarenaresetter.commands;

import org.akatriggered.betterarenaresetter.Arena;
import org.akatriggered.betterarenaresetter.ArenaCommand;
import org.akatriggered.betterarenaresetter.ConfigManager;
import org.akatriggered.betterarenaresetter.BetterArenaResetter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ResetCommand extends ArenaCommand {

    public ResetCommand() {
        super("reset", "Reset an arena", "/arena reset <arena> [speed]", new String[] {"revert"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!sender.hasPermission("BetterArenaResetter.reset")) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " You don't have permission to run this command!");
            return;
        }

        if (!BetterArenaResetter.INSTANCE.isReady()) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " Arenas have not finished loading yet!");
            return;
        }

        if (args.size() == 0) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " Reset an arena by running /arena reset <arena> [speed]");
            return;
        }

        if (!Arena.arenas.containsKey(args.get(0).toLowerCase())) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " Arena by that name not found!");
            return;
        }

        /*if (Arena.arenas.get(args.get(0).toLowerCase()).isBeingReset()) {
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " That arena is currently being reset!");
            return;
        }*/

        boolean silent = false;

        //Check for any "-" arguments and remove them beforehand
        for (Iterator<String> iterator = args.iterator(); iterator.hasNext(); ) {
            String next =  iterator.next();

            if (next.startsWith("-")) {
                String argument = next.substring(1);

                if (argument.equalsIgnoreCase("silent") || argument.equalsIgnoreCase("s")) {
                    silent = true;
                    iterator.remove();
                } else {
                    sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " Invalid argument \"" + argument + "\"!");
                    return;
                }
            }
        }

        ResetSpeed speed = ResetSpeed.NORMAL;
        if (args.size() >= 2) {
            speed = ResetSpeed.getSpeed(args.get(1));
        }

        Arena arena = Arena.arenas.get(args.get(0).toLowerCase());

        if (speed == ResetSpeed.INSTANT) { //If they want to reset the arena instantly, warn them beforehand.
            if (!sender.hasPermission("BetterArenaResetter.reset.instant")) {
                sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.RED + " You don't have permission to reset arenas instantly!");
                return;
            }
            ResetSpeed finalSpeed = speed;
            boolean finalSilent = silent;
            ConfirmCommand.confirmTasks.put(sender, () -> resetArena(arena, finalSpeed, sender, finalSilent));
            new BukkitRunnable() {
                @Override
                public void run() {
                    ConfirmCommand.confirmTasks.remove(sender);
                }
            }.runTaskLater(BetterArenaResetter.INSTANCE, 20 * 60); //Remove the task after a min
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.YELLOW + " Are you sure you want to reset arena \"" + arena.getName() + "\" instantly?");
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.YELLOW + " To confirm this, use " + ChatColor.RED + "/arena confirm");
        } else {
            resetArena(arena, speed, sender, silent);
        }

    }

    private void resetArena(Arena arena, ResetSpeed speed, CommandSender sender, boolean silent) {
        if (arena.isBeingReset()) {
            arena.setResetSpeed(speed.getAmount() / 20);
            arena.setResetSilence(silent);
            sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.GREEN + " Changing arena reset speed to \"" + speed.name().replace("_", " ").toLowerCase(Locale.ROOT) + "\"!");
            return;
        }

        sender.sendMessage(BetterArenaResetter.PREFIX + ChatColor.GREEN + " Resetting arena \"" + arena.getName() + "\"!");
        long time = System.currentTimeMillis();
        arena.reset(speed.getAmount() / 20, sender, silent);
    }


    public enum ResetSpeed {

        VERY_SLOW(ConfigManager.BLOCKS_RESET_PER_SECOND_VERYSLOW, "veryslow"),
        SLOW(ConfigManager.BLOCKS_RESET_PER_SECOND_SLOW),
        NORMAL(ConfigManager.BLOCKS_RESET_PER_SECOND_NORMAL),
        FAST(ConfigManager.BLOCKS_RESET_PER_SECOND_FAST),
        VERY_FAST(ConfigManager.BLOCKS_RESET_PER_SECOND_VERYFAST, "veryfast"),
        EXTREMELY_FAST(ConfigManager.BLOCKS_RESET_PER_SECOND_EXTREME, "extremelyfast", "extreme"),
        INSTANT(Integer.MAX_VALUE, "instantly");

        private int amount;
        private String[] alias;

        ResetSpeed(int amount, String... alias) {
            this.amount = amount;
            this.alias = alias;
        }

        public int getAmount() {
            return amount;
        }

        public static ResetSpeed getSpeed(String string) {
            for (ResetSpeed speed : values()) {
                if (string.equalsIgnoreCase(speed.name()) || Arrays.asList(speed.alias).contains(string.toLowerCase())) {
                    return speed;
                }
            }
            return NORMAL;
        }

        /**
         * Re-set all the reset amount values from config
         */
        public static void reload() {
            VERY_SLOW.amount = ConfigManager.BLOCKS_RESET_PER_SECOND_VERYSLOW;
            SLOW.amount = ConfigManager.BLOCKS_RESET_PER_SECOND_SLOW;
            NORMAL.amount = ConfigManager.BLOCKS_RESET_PER_SECOND_NORMAL;
            FAST.amount = ConfigManager.BLOCKS_RESET_PER_SECOND_FAST;
            VERY_FAST.amount = ConfigManager.BLOCKS_RESET_PER_SECOND_VERYFAST;
            EXTREMELY_FAST.amount = ConfigManager.BLOCKS_RESET_PER_SECOND_EXTREME;
        }
    }

    @Override
    protected List<String> getTabCompletion(CommandSender sender, List<String> args) {
        List<String> completions = new ArrayList<>();
        if (args.size() <= 1) {
            completions.addAll(Arena.arenas.keySet());
            completions.sort(Comparator.naturalOrder());
        } else if (args.size() == 2) {
            completions.addAll(Arrays.asList(new String[] {"veryslow", "slow", "normal", "fast", "veryfast", "extreme"}));
            if (sender.hasPermission("BetterArenaResetter.reset.instant")) {
                completions.add("instant");
            }
        } else if (args.size() == 3) {
            completions.add("-silent");
        }

        return completions;
    }
}
