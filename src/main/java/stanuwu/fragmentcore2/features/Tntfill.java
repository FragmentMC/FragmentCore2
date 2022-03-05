package stanuwu.fragmentcore2.features;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import stanuwu.fragmentcore2.FragmentCore2;
import stanuwu.fragmentcore2.helpers.Helper;

public class Tntfill implements CommandExecutor, Listener {
    public Tntfill() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        Location location = player.getLocation();

        int radius = FragmentCore2.config.getInt("fragmentcore.cannoning.tntfill-range");
        Block middle = location.getBlock();
        for (int x = radius; x >= -radius; x--) {
            for (int y = radius; y >= -radius; y--) {
                for (int z = radius; z >= -radius; z--) {
                    if (middle.getRelative(x, y, z).getType() == Material.DISPENSER) {
                        Dispenser d = (Dispenser) middle.getRelative(x, y, z).getState();
                        d.getInventory().addItem(new ItemStack(Material.TNT, 576));
                    }
                }
            }
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("Filled Dispensers")));



        return true;
    }
}
