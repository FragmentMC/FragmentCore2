package stanuwu.fragmentcore2.features;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import stanuwu.fragmentcore2.helpers.Helper;

public class WaterproofRedstone implements Listener {
    public WaterproofRedstone() {}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        Material mat = event.getToBlock().getType();
        if (Helper.waterproofBlocks.contains(mat) || Helper.buttonsSlow.contains(mat) || Helper.buttonsFast.contains(mat)) {
            event.setCancelled(true);
        }
    }
}