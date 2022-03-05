package stanuwu.fragmentcore2.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import stanuwu.fragmentcore2.helpers.Helper;

public class Discord implements CommandExecutor, Listener {

    public Discord() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.ParsePrefix(Helper.getConfigString("cosmetic", "discord-invite"))));
        return true;
    }
}



