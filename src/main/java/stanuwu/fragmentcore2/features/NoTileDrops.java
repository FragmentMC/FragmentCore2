package stanuwu.fragmentcore2.features;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import stanuwu.fragmentcore2.FragmentCore2;

public class NoTileDrops implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityExplode(EntityExplodeEvent e) {
        if (FragmentCore2.config.getBoolean("fragmentcore.cannoning.no-tile-drops")) {
            for (Block b: e.blockList()) {
                if (!b.getState().getClass().getName().endsWith("CraftBlockState")) {
                    b.setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (!e.getBlock().getState().getClass().getName().endsWith("CraftBlockState")){
            e.setDropItems(false);
        }
    }
}
