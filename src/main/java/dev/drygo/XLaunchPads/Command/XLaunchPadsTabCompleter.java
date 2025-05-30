package dev.drygo.XLaunchPads.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class XLaunchPadsTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase()) || (sender.hasPermission("xlaunchpads.admin") || sender.isOp())) {
                completions.add("reload");
            }
            if ("set".startsWith(args[0].toLowerCase()) || (sender.hasPermission("xlaunchpads.admin") || sender.isOp())) {
                completions.add("reload");
            }
            if ("set".startsWith(args[0].toLowerCase()) || (sender.hasPermission("xlaunchpads.admin") || sender.isOp())) {
                completions.add("help");
            }
        }
        return completions;
    }
}
