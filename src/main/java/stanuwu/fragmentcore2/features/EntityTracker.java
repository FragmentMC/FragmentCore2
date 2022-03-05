package stanuwu.fragmentcore2.features;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import stanuwu.fragmentcore2.FragmentCore2;
import stanuwu.fragmentcore2.helpers.Helper;


public class EntityTracker implements Listener, CommandExecutor {
    private final FragmentCore2 plugin;
    private final HashMap<UUID, trackerInstance> trackers = new HashMap<>();
    public static class trackerInstance {
        public HashSet<Block> trackedBlocks;
        public HashSet<FallingBlock> trackedFalling;
        public HashSet<TNTPrimed> trackedTNT;
        public trackerInstance() {
            trackedBlocks = new HashSet<>();
            trackedFalling = new HashSet<>();
            trackedTNT = new HashSet<>();
        }
    }

    public EntityTracker(FragmentCore2 plugin) {
        this.plugin = plugin;
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Map.Entry<UUID, trackerInstance> entry : trackers.entrySet()) {
                    HashSet<FallingBlock> toRemoveFalling = new HashSet<>();
                    HashSet<TNTPrimed> toRemoveTNT = new HashSet<>();
                    trackerInstance t = entry.getValue();
                    Player p = plugin.getServer().getPlayer(entry.getKey());
                    for (TNTPrimed tnt : t.trackedTNT) {
                        if (tnt.isDead()) {
                            toRemoveTNT.add(tnt);
                        } else {
                            Location loc = tnt.getLocation();
                            p.sendMessage(String.format("§7[§3i§7] §fTNT: Ticks: §b%s §7[§f%.5f, %.5f, %.5f§7]", tnt.getTicksLived(), loc.getX(), loc.getY(), loc.getZ()));
                        }
                    }
                    for (FallingBlock falling : t.trackedFalling) {
                        if (falling.isDead()) {
                            toRemoveFalling.add(falling);
                        } else {
                            Location loc = falling.getLocation();
                            p.sendMessage(String.format("§7[§3i§7] §fFB: Ticks: §b%s §7[§f%.5f, %.5f, %.5f§7]", falling.getTicksLived(), loc.getX(), loc.getY(), loc.getZ()));
                        }
                    }
                    for (FallingBlock falling : toRemoveFalling) {
                        Location loc = falling.getLocation();
                        t.trackedFalling.remove(falling);
                        p.sendMessage(String.format("§7[§3i§7] §fDead FB: Ticks: §b%s §7[§f%.5f, %.5f, %.5f§7]", falling.getTicksLived(), loc.getX(), loc.getY(), loc.getZ()));
                    }
                    for (TNTPrimed tnt : toRemoveTNT) {
                        Location loc = tnt.getLocation();
                        t.trackedTNT.remove(tnt);
                        p.sendMessage(String.format("§7[§3i§7] §fDead TNT: Ticks: §b%s §7[§f%.5f, %.5f, %.5f§7]", tnt.getTicksLived(), loc.getX(), loc.getY(), loc.getZ()));
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            String commandName = command.getName();
            Player p = (Player) sender;

            if (commandName.equalsIgnoreCase("ct")) {
                if (trackers.get(p.getUniqueId()) == null) {
                    trackers.put(p.getUniqueId(), new trackerInstance());
                }
                trackerInstance t = trackers.get(p.getUniqueId());
                t.trackedTNT.clear();
                t.trackedBlocks.clear();
                t.trackedFalling.clear();
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("All Trackers Removed")));
            }
        }
        return true;
    }
    @EventHandler
    public void onTNTIgnite(EntitySpawnEvent event) {
        Entity e = event.getEntity();
        if (event.getEntityType() == EntityType.PRIMED_TNT) {
            for (Map.Entry<UUID, trackerInstance> entry : trackers.entrySet()) {
                trackerInstance t = entry.getValue();
                Block b = e.getLocation().getBlock();
                if (t.trackedBlocks.contains(b)) {
                    t.trackedBlocks.remove(b);
                    t.trackedTNT.add((TNTPrimed) e);
                    plugin.getServer().getPlayer(entry.getKey()).sendMessage(String.format("§7[§3i§7] §fTNT PRIMED §7[§f%s, %s, %s§7]", b.getX(), b.getY(), b.getZ()));
                }
            }
        }
    }
    @EventHandler
    public void onTrackedFall(EntityChangeBlockEvent event) {
        Block b = event.getBlock();
        for (Map.Entry<UUID, trackerInstance> entry : trackers.entrySet()) {
            trackerInstance t = entry.getValue();
            if (t.trackedBlocks.contains(b)) {
                Player p = plugin.getServer().getPlayer(entry.getKey());
                t.trackedBlocks.remove(b);
                t.trackedFalling.add((FallingBlock) event.getEntity());
                p.sendMessage(String.format("§7[§3i§7] §fSOLID -> FB §7[§f%s, %s, %s§7]", b.getX(), b.getY(), b.getZ()));
            } else if (t.trackedFalling.contains(event.getEntity())) {
                Player p = plugin.getServer().getPlayer(entry.getKey());
                t.trackedFalling.remove(event.getEntity());
                t.trackedBlocks.add(b);
                p.sendMessage(String.format("§7[§3i§7] §fFB -> SOLID §7[§f%s, %s, %s§7]", b.getX(), b.getY(), b.getZ()));
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        for (Map.Entry<UUID, trackerInstance> entry : trackers.entrySet()) {
            trackerInstance t = entry.getValue();
            if (t.trackedBlocks.contains(b)) {
                plugin.getServer().getPlayer(entry.getKey()).sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix(String.format("Broken Tracked Block [%s, %s, %s]", b.getX(), b.getY(), b.getZ()))));
                t.trackedBlocks.remove(b);
            }
        }
    }
    @EventHandler
    public void onDispenserFire(BlockDispenseEvent event) {
        Block b = event.getBlock();
        if (b.getType() == Material.DISPENSER) {
            boolean spawn = false;
            for (Map.Entry<UUID, trackerInstance> entry : trackers.entrySet()) {
                trackerInstance t = entry.getValue();
                if (t.trackedBlocks.contains(b)) {
                    spawn = true;
                    break;
                }
            }
            if (spawn) {
                Dispenser d = (Dispenser) b.getBlockData();
                BlockFace face = d.getFacing();
                TNTPrimed tnt = (TNTPrimed) b.getWorld().spawnEntity(b.getRelative(face).getLocation().add(0.5, 0.0, 0.5), EntityType.PRIMED_TNT);
                for (Map.Entry<UUID, trackerInstance> entry : trackers.entrySet()) {
                    trackerInstance t = entry.getValue();
                    if (t.trackedBlocks.contains(b)) {
                        plugin.getServer().getPlayer(entry.getKey()).sendMessage(String.format("§7[§3i§7] §fDispenser: §b%s §7[§f%s, %s, %s§7]",face.name(), b.getX(), b.getY(), b.getZ()));
                        t.trackedTNT.add(tnt);
                    }
                }
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onTNTExplode(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        Location loc = e.getLocation();
        for (Map.Entry<UUID, trackerInstance> entry : trackers.entrySet()) {
            Player p = plugin.getServer().getPlayer(entry.getKey());
            trackerInstance t = entry.getValue();
            if (t.trackedTNT.contains(e)) {
                p.sendMessage(String.format("§7[§4!§7] §fTNT: §4EXPLODE §7[§f%.5f, %.5f, %.5f§7]", loc.getX(), loc.getY(), loc.getZ()));
                t.trackedTNT.remove(e);
            }
        }
    }
    @EventHandler
    public void onBlockHit(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getInventory().getItemInMainHand().getType().equals(Material.getMaterial(Helper.getConfigString("cannoning", "entitytracker-item").toUpperCase()))) {
            if (event.getClickedBlock() != null) {
                Action a = event.getAction();
                if (a == Action.RIGHT_CLICK_BLOCK || a == Action.LEFT_CLICK_BLOCK) {
                    Block b = event.getClickedBlock();
                    if (Helper.fallingBlocks.contains(b.getType()) ||Helper.extraTrackables.contains(b.getType())) {
                        Location loc = b.getLocation();
                        if (trackers.get(p.getUniqueId()) == null) {
                            trackers.put(p.getUniqueId(), new trackerInstance());
                        }
                        trackerInstance t = trackers.get(p.getUniqueId());
                        if (!t.trackedBlocks.contains(b)) {
                            t.trackedBlocks.add(b);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix(String.format("Tracking %s [%s, %s, %s]", formatName(b.getType().name()), loc.getX(), loc.getY(), loc.getZ()))));
                        } else {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix(String.format("Already Tracking %s [%s, %s, %s]", formatName(b.getType().name()), loc.getX(), loc.getY(), loc.getZ()))));
                        }
                    }
                }
            }
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void pistonExtendEvent(BlockPistonExtendEvent event) {
        for (Map.Entry<UUID, trackerInstance> entry : trackers.entrySet()) {
            trackerInstance t = entry.getValue();
            HashSet<Block> blocks = new HashSet<>();
            for (Block b : event.getBlocks()) {
                if (t.trackedBlocks.contains(b)) {
                    t.trackedBlocks.remove(b);
                    blocks.add(b.getRelative(event.getDirection()));
                }
            }
            t.trackedBlocks.addAll(blocks);
        }
    }
    @EventHandler
    public void pistonRetractEvent(BlockPistonRetractEvent event) {
        for (Block b : event.getBlocks()) {
            if (Helper.fallingBlocks.contains(b.getType()) || Helper.extraTrackables.contains(b.getType())) {
                for (Map.Entry<UUID, trackerInstance> entry : trackers.entrySet()) {
                    trackerInstance t = entry.getValue();
                    if (t.trackedBlocks.contains(b)) {
                        t.trackedBlocks.remove(b);
                        t.trackedBlocks.add(b.getRelative(event.getDirection()));
                    }
                }
            }
        }
    }
    public String formatName(String name) {
        String[] split = name.split("_");
        for (int i=0;i<split.length;i++) {
            String s = split[i];
            split[i] = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
        return String.join(" ", split);
    }
}