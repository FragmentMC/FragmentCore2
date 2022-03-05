package stanuwu.fragmentcore2.features;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import stanuwu.fragmentcore2.FragmentCore2;
import stanuwu.fragmentcore2.helpers.Helper;

import java.util.HashMap;
import java.util.UUID;

public class Fire implements Listener, CommandExecutor {
    private final FragmentCore2 plugin;
    private final HashMap<UUID, Location> buttons;
    private final HashMap<UUID, Location> levers;

    public Fire(FragmentCore2 plugin) {
        this.plugin = plugin;
        this.buttons = new HashMap<>();
        this.levers = new HashMap<>();
    }

    //causes some rare weird budding issues as well as observers are able to detect /fire on the block behind the button - maybe ill fix this in the future
    void updatePowered(Block block) {
        if (block.getType().isOccluding()) {
            BlockData blockData = block.getBlockData().clone();
            if (block.getState() instanceof InventoryHolder) {
                ItemStack[] abInv = ((InventoryHolder) block.getState()).getInventory().getContents().clone();
                block.setType(Material.BARRIER);
                block.setBlockData(blockData);
                ((InventoryHolder) block.getState()).getInventory().setContents(abInv);
            } else {
                block.setType(Material.BARRIER);
                block.setBlockData(blockData);
            }
        }
    }

    Block getAttached(Block b) {
        Switch button = (Switch) b.getBlockData();
        BlockFace buttonFacing = button.getFacing();
        switch (button.getAttachedFace()) {
            case CEILING:
                buttonFacing = BlockFace.DOWN;
                break;
            case FLOOR:
                buttonFacing = BlockFace.UP;
                break;
        }
        return b.getRelative(buttonFacing.getOppositeFace());
    }

    int buttonLength(Material m) {
        if (Helper.buttonsSlow.contains(m)) {
            return 31;
        } else {
            return 21;
        }
    }

    boolean isButton(Material m) {
        return Helper.buttonsFast.contains(m) ||Helper.buttonsSlow.contains(m);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            UUID uuid = p.getUniqueId();
            World world = p.getWorld();
            if (command.getName().equalsIgnoreCase("fire")) {
                if (this.buttons.containsKey(uuid)) {
                    Block b = world.getBlockAt(this.buttons.get(uuid));
                    if (isButton(b.getType())) {
                        Switch button = (Switch) b.getBlockData();
                        if (!button.isPowered()) {
                            button.setPowered(true);
                            b.setBlockData(button, true);
                            Block attachedBlock = getAttached(b);
                            updatePowered(attachedBlock);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix("Fired")));
                            int unpowerAfter = buttonLength(b.getType());
                            new BukkitRunnable(){
                                @Override
                                public void run(){
                                    button.setPowered(false);
                                    if (isButton(b.getType())) {
                                        b.setBlockData(button, true);
                                        updatePowered(attachedBlock);
                                    }
                                }
                            }.runTaskLater(plugin, unpowerAfter);
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix("Button Broken")));
                    }
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix("No Button Selected")));
                }
            } else if (command.getName().equalsIgnoreCase("lever")) {
                if (this.levers.containsKey(uuid)) {
                    Block b = world.getBlockAt(this.levers.get(uuid));
                    if (b.getType().equals(Material.LEVER)) {
                        Switch lever = (Switch) b.getBlockData();
                        lever.setPowered(!lever.isPowered());
                        b.setBlockData(lever, true);
                        Block attachedBlock = getAttached(b);
                        updatePowered(attachedBlock);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix("Lever Toggled")));
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix("Lever Broken")));
                    }
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix("No Lever Selected")));
                }
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onButtonPress(PlayerInteractEvent event) {
        if (!(event.useInteractedBlock() == Event.Result.DENY)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block b = event.getClickedBlock();
                Player p = event.getPlayer();
                UUID uuid = p.getUniqueId();
                if (isButton(b.getType())) {
                    if (!(this.buttons.containsKey(uuid) && this.buttons.get(uuid).equals(b.getLocation()))) {
                        this.buttons.put(uuid, b.getLocation());
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix(String.format("Button Added " + ("[%s, %s, %s]"), b.getX(), b.getY(), b.getZ()))));
                    }
                } else if (b.getType().equals(Material.LEVER)) {
                    if (!(this.levers.containsKey(uuid) && this.levers.get(uuid).equals(b.getLocation()))) {
                        this.levers.put(uuid, b.getLocation());
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.WithPrefix(String.format("Lever Added " + ("[%s, %s, %s]"), b.getX(), b.getY(), b.getZ()))));
                    }
                }
            }
        }
    }
}
