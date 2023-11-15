package stanuwu.fragmentcore2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Config implements CommandExecutor {
    private final Plugin plugin;

    public static List<Material> protectedBlocks = new ArrayList<>();
    public static List<Material> unbreakableBlocks = new ArrayList<>();

    public Config(Plugin plugin) {
        this.plugin = plugin;
    }

    public static void initConfig(Plugin plugin) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
        for (Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
            plugin.getConfig().addDefault(entry.getKey(), entry.getValue());
        }
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        for(String s : config.getStringList("fragmentcore.cannoning.protection-blocks")){
            protectedBlocks.add(Material.valueOf(s.toUpperCase(Locale.ROOT)));
        }
        for(String s : config.getStringList("fragmentcore.cannoning.unbreakable-blocks")){
            unbreakableBlocks.add(Material.valueOf(s.toUpperCase(Locale.ROOT)));
        }
    }

    @Override
    public boolean onCommand(CommandSender author, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("reload")) {
            try {
                plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
                author.sendMessage("Reloaded!");
            } catch (Exception e) {
                e.printStackTrace();
                author.sendMessage(ChatColor.RED + "An Exception Occurred!");
            }
        }
        return true;
    }
}
