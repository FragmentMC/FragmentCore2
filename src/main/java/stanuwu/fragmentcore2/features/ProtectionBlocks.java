package stanuwu.fragmentcore2.features;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import stanuwu.fragmentcore2.FragmentCore2;

import java.util.Iterator;

public class ProtectionBlocks implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!e.isCancelled()) {
            boolean isProtected = false;
            Location explosionLoc = e.getLocation();
            World world = explosionLoc.getWorld();
            for (int y = -64; y <= 320; y++) {
                Material materialType = world.getBlockAt(explosionLoc.getBlockX(), y, explosionLoc.getBlockZ()).getType();
                if(FragmentCore2.config.getStringList("fragmentcore.cannoning.protection-blocks").contains(materialType.name().toLowerCase())){
                    isProtected = true;
                    break;
                }
            }
            if (isProtected) {
                e.blockList().clear();
            } else {
                Iterator<Block> iterator = e.blockList().iterator();
                while(iterator.hasNext())
                {
                    Block b = iterator.next();
                    if(FragmentCore2.config.getStringList("fragmentcore.cannoning.unbreakable-blocks").contains(b.getType().name().toLowerCase())){
                        iterator.remove();
                        continue;
                    }
                    for (int y = -64; y <= 320; y++) {
                        Material materialType = world.getBlockAt(b.getX(), y, b.getZ()).getType();
                        if(FragmentCore2.config.getStringList("fragmentcore.cannoning.protection-blocks").contains(materialType.name().toLowerCase())){
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
        }
    }
}
