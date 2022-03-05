package stanuwu.fragmentcore2.features;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import stanuwu.fragmentcore2.FragmentCore2;
import stanuwu.fragmentcore2.helpers.Helper;

public class ClearEntities implements CommandExecutor {

    public ClearEntities() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        int r = FragmentCore2.config.getInt("fragmentcore.cannoning.clearentity-range");
        int count = 0;
        for(Entity entity : player.getNearbyEntities(r, r, r)) {
            if(entity instanceof TNTPrimed | entity instanceof FallingBlock)
                count = count+1;
                entity.remove();
        }
        if(count<1){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("No Entities Found")));
        }else{
            if(count==1){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix(count+" Entity Cleared")));
            }else{
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix(count+" Entities Cleared")));
            }
        }
        return true;
    }
}