package stanuwu.fragmentcore2.features;

import org.bukkit.*;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import stanuwu.fragmentcore2.FragmentCore2;
import stanuwu.fragmentcore2.helpers.Helper;

import java.util.*;

public class MultiDispenser implements CommandExecutor, Listener, TabCompleter {
    final private FragmentCore2 plugin;
    final private HashMap<Location, List<Integer>> dispensers;

    public MultiDispenser(FragmentCore2 plugin) {
        this.plugin = plugin;
        this.dispensers = new HashMap<>();
    }

    ItemStack getDispenser(int amount, int fuse) {
        ItemStack item = new ItemStack(Material.DISPENSER, 1);
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1530);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, amount);
        item.addUnsafeEnchantment(Enchantment.DURABILITY, fuse);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Helper.getConfigString("cosmetic", "multidispenser-name").replaceAll("%amount%", Integer.toString(amount)).replaceAll("%fuse%", Integer.toString(fuse))));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label , String[] args) {
        List<String> l = new ArrayList<>();
        if (args.length == 1) {
            l = new LinkedList<>(Arrays.asList("10", "50", "100", "500", "1000", "5000", "10000", "50000", "100000"));
            int max = FragmentCore2.config.getInt("fragmentcore.cannoning.max-multitnt");
            l.removeIf(s -> Integer.parseInt(s) >= max);
            l.add(Integer.toString(max));
        } else if (args.length == 2) {
            l = Arrays.asList("0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "110", "120");
        }
        return l;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        int amount;
        int fuse;
        if (args.length < 2) {
            fuse = 80;
        } else {
            fuse =  Integer.parseInt(args[1]);
            if (fuse < 0) {
                fuse = 0;
            }
        }
        if (args.length < 1) {
            amount = 1;
        } else {
            amount = Integer.parseInt(args[0]);
            if (amount < 1) {
                amount = 1;
            }
            int max = FragmentCore2.config.getInt("fragmentcore.cannoning.max-multitnt");
            if (amount > max) {
                amount = max;
            }
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("Received MultiDispenser")));
        player.getInventory().addItem(getDispenser(amount, fuse));
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDispense(BlockDispenseEvent event) {
        if (!event.isCancelled()) {
            Location dispenserLoc = event.getBlock().getLocation();
            if (dispensers.containsKey(dispenserLoc)) {
                World world = event.getBlock().getWorld();
                List<Integer> data = dispensers.get(dispenserLoc);
                int amount = data.get(0);
                int fuse = data.get(1);
                Location summonLoc = event.getBlock().getRelative(((Directional)event.getBlock().getBlockData()).getFacing()).getLocation().add(0.5, 0, 0.5);
                Material summonMat = event.getItem().getType();
                if (summonMat.equals(Material.TNT)) {
                    for (int i = 0; i < amount; i++) {
                        TNTPrimed tnt = (TNTPrimed)world.spawnEntity(summonLoc, EntityType.PRIMED_TNT);
                        tnt.setFuseTicks(fuse);
                        tnt.setVelocity(new Vector(0, 0, 0));
                    }
                    event.setCancelled(true);
                } else if (Helper.fallingBlocks.contains(summonMat)) {
                    for (int i = 0; i < amount; i++) {
                        world.spawnFallingBlock(summonLoc, summonMat.createBlockData());
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            if (event.getBlock().getType() == Material.DISPENSER) {
                Map<Enchantment, Integer> enchants = event.getItemInHand().getEnchantments();
                int amount = enchants.getOrDefault(Enchantment.PROTECTION_EXPLOSIONS, 0);
                int fuse = enchants.getOrDefault(Enchantment.DURABILITY, 0);
                if (event.getItemInHand().equals(getDispenser(amount, fuse))) {
                    Player player = event.getPlayer();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix(String.format("Placed MultiDispenser [Amount: %s, Fuse:%s]", amount, fuse))));
                    dispensers.put(event.getBlock().getLocation(), Arrays.asList(amount, fuse));
                    new BukkitRunnable() {
                        final Location loc = event.getBlock().getLocation();
                        @Override
                        public void run() {
                            if(!loc.getBlock().getType().equals(Material.DISPENSER)) {
                                dispensers.remove(loc);
                                this.cancel();
                            }
                        }
                    }.runTaskTimerAsynchronously(this.plugin, 200, 200);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            if (event.getBlock().getType().equals(Material.DISPENSER)) {
                if (dispensers.containsKey(event.getBlock().getLocation())) {
                    dispensers.remove(event.getBlock().getLocation());
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.WithPrefix("Broke MultiDispenser")));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if(!event.isCancelled()) {
            event.blockList().forEach(b -> {
                if(b.getType().equals(Material.DISPENSER)) {
                    dispensers.remove(b.getLocation());
                }
            });
        }
    }
}