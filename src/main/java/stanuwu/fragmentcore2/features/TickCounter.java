package stanuwu.fragmentcore2.features;

import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import stanuwu.fragmentcore2.helpers.Helper;

import java.util.HashMap;
import java.util.UUID;

public class TickCounter implements Listener {

    private final HashMap<UUID, Integer> tickTracker;
    private final HashMap<UUID, Boolean> gametickGen;

    public TickCounter() {
        this.tickTracker = new HashMap<>();
        this.gametickGen = new HashMap<>();
    }

    @EventHandler
    public void onRedstoneBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p.getInventory().getItemInMainHand().getType().equals(Material.getMaterial(Helper.getConfigString("cannoning", "tickcounter-item").toUpperCase()))) {
            Block b = event.getBlock();
            if (b.getType() == Material.REPEATER) {
                UUID playerUUID = p.getUniqueId();
                Repeater rep = (Repeater) b.getBlockData();
                if (tickTracker.containsKey(playerUUID)) {
                    tickTracker.put(playerUUID, tickTracker.get(playerUUID) + rep.getDelay()*2);
                } else {
                    tickTracker.put(playerUUID, rep.getDelay()*2);
                }
                gametickGen.put(playerUUID, false);
                p.playNote(p.getLocation(), Instrument.SNARE_DRUM, new Note(tickTracker.get(p.getUniqueId()) % 25));
                showTicks(p, rep.getDelay()*2);
            }
            else if (b.getType() == Material.COMPARATOR || b.getType() == Material.OBSERVER) {
                UUID playerUUID = p.getUniqueId();
                if (tickTracker.containsKey(playerUUID)) {
                    tickTracker.put(playerUUID, tickTracker.get(playerUUID) + 2);
                } else {
                    tickTracker.put(playerUUID, 2);
                }
                gametickGen.put(playerUUID, false);
                p.playNote(p.getLocation(), Instrument.SNARE_DRUM, new Note(tickTracker.get(p.getUniqueId()) % 25));
                showTicks(p, 2);
            }
            else if (b.getType() == Material.STICKY_PISTON || b.getType() == Material.PISTON) {
                UUID playerUUID = p.getUniqueId();
                int tta = 2;
                if (gametickGen.containsKey(playerUUID) && gametickGen.get(playerUUID)) {
                    tta++;
                    gametickGen.put(playerUUID, false);
                } else {
                    gametickGen.put(playerUUID, true);
                }
                if (tickTracker.containsKey(playerUUID)) {
                    tickTracker.put(playerUUID, tickTracker.get(playerUUID) + tta);
                } else {
                    tickTracker.put(playerUUID, tta);
                }
                p.playNote(p.getLocation(), Instrument.SNARE_DRUM, new Note(tickTracker.get(p.getUniqueId()) % 25));
                showTicks(p, tta);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Action a = event.getAction();
        Player p = event.getPlayer();
        if (p.getInventory().getItemInMainHand().getType().equals(Material.getMaterial(Helper.getConfigString("cannoning", "tickcounter-item").toUpperCase()))) {
            if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
                tickTracker.put(p.getUniqueId(), 0);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("Reset Tick Counter")));
                event.setCancelled(true);
            } else if (a == Action.LEFT_CLICK_AIR) {
                if (tickTracker.containsKey(p.getUniqueId())) {
                    showTicks(p);
                } else {
                    tickTracker.put(p.getUniqueId(), 0);
                }
            }
        }
    }

    public void showTicks(Player p, int added) {
        long ticks = tickTracker.get(p.getUniqueId());
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix(String.format("Counter:+%s [GT:%s RT:%s S:%s]", added, ticks, ticks/2.0, ticks / 20.0))));
    }
    public void showTicks(Player p) {
        showTicks(p, 0);
    }
}
