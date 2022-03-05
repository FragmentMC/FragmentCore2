package stanuwu.fragmentcore2.features;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import stanuwu.fragmentcore2.FragmentCore2;
import stanuwu.fragmentcore2.helpers.Helper;

import java.util.ArrayList;
import java.util.List;

public class MagicSand implements CommandExecutor, Listener, TabCompleter {
    private final FragmentCore2 plugin;

    public MagicSand(FragmentCore2 plugin) {
        this.plugin = plugin;
    }

    ItemStack getSandItem(Material block) {
        ItemStack item = new ItemStack(block, 1);
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1530);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Helper.getConfigString("cosmetic", "magicsand-name")));
        item.setItemMeta(meta);
        return item;
    }

    void stackDownReplace(Material initial, Material replacement, Location below) {
        Block adjBlock = below.getBlock().getRelative(BlockFace.DOWN);
        while (adjBlock.getType().equals(initial)) {
            adjBlock.setType(replacement);
            adjBlock = adjBlock.getRelative(BlockFace.DOWN);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label , String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (Material m: Helper.fallingBlocks) {
                list.add(m.name().toLowerCase());
            }
        }
        return list;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        Material block;
        if (args.length < 1 || !Helper.fallingBlocks.contains(Material.getMaterial(args[0].toUpperCase()))) {
            block = Material.SAND;
        } else {
            block = Material.getMaterial(args[0].toUpperCase());
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("Received MagicSand")));
        player.getInventory().addItem(getSandItem(block));
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            if (getSandItem(event.getItemInHand().getType()).equals(event.getItemInHand())) {
                Material msBlock = Material.getMaterial(Helper.getConfigString("cannoning", "magicsand-block").toUpperCase());
                Material msMaterial = event.getItemInHand().getType();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("Placed MagicSand")));
                event.getBlock().setType(msBlock);
                Location msLoc = event.getBlock().getLocation();
                stackDownReplace(Material.AIR, msMaterial, msLoc);
                new BukkitRunnable() {
                    final World world = player.getWorld();
                    final Location spawnLoc = msLoc.clone().add(0.5, -0.7, 0.5);
                    FallingBlock last = null;
                    public void run() {
                        if(msLoc.getBlock().getType().equals(msBlock))
                        {
                            if(msLoc.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                                if (!(last != null && last.getBoundingBox().overlaps(msLoc.getBlock().getBoundingBox()))) {
                                    last = world.spawnFallingBlock(spawnLoc, msMaterial.createBlockData());
                                }
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("Broke MagicSand")));
                            stackDownReplace(msMaterial, Material.AIR, msLoc);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(this.plugin, 0, 1);
            }
        }
    }
}
