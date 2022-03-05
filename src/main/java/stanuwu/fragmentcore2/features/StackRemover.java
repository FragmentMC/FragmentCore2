package stanuwu.fragmentcore2.features;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import stanuwu.fragmentcore2.FragmentCore2;
import stanuwu.fragmentcore2.helpers.Helper;

public class StackRemover implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Material item = event.getPlayer().getInventory().getItemInMainHand().getType();
        if(item.equals(Material.getMaterial(Helper.getConfigString("cannoning", "stackremover-item").toUpperCase()))){
            Player player = event.getPlayer();
            Block block = player.getTargetBlockExact(FragmentCore2.config.getInt("fragmentcore.cannoning.stackremover-range"));
            Material material = block.getType();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("Removed Stack")));
            if(Helper.stackContents.contains(material)){
                for(int y = -64; y < 320; y++){
                    Location newlocc = new Location(block.getWorld(), block.getX(), y, block.getZ());
                    if(Helper.stackContents.contains(newlocc.getBlock().getType())){
                        BlockBreakEvent bbe = new BlockBreakEvent(newlocc.getBlock(), player);
                        Bukkit.getServer().getPluginManager().callEvent(bbe);
                        if (!bbe.isCancelled()) {
                            newlocc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}
