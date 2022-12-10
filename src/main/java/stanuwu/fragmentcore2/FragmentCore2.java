package stanuwu.fragmentcore2;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import stanuwu.fragmentcore2.commands.Discord;
import stanuwu.fragmentcore2.commands.Upload;
import stanuwu.fragmentcore2.events.JoinEvent;
import stanuwu.fragmentcore2.features.*;

public final class FragmentCore2 extends JavaPlugin {
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        //config
        Config.initConfig(this);
        config=this.getConfig();
        getCommand("reload").setExecutor(new Config(this));

        //predefined
        Fire fire = new Fire(this);
        EntityTracker entityTracker = new EntityTracker(this);
        MagicSand magicSand = new MagicSand(this);
        MultiDispenser multiDispenser = new MultiDispenser(this);

        //register commands
        getCommand("discord").setExecutor(new Discord());
        getCommand("upload").setExecutor(new Upload());
        getCommand("ce").setExecutor(new ClearEntities());
        getCommand("fire").setExecutor(fire);
        getCommand("lever").setExecutor(fire);
        getCommand("tntfill").setExecutor(new Tntfill());
        getCommand("ct").setExecutor(entityTracker);
        getCommand("magicsand").setExecutor(magicSand);
        getCommand("magicsand").setTabCompleter(magicSand);
        getCommand("multi").setExecutor(multiDispenser);
        getCommand("multi").setTabCompleter(multiDispenser);

        //register events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinEvent(), this);
        pm.registerEvents(new StackRemover(), this);
        pm.registerEvents(new NoTileDrops(), this);
        pm.registerEvents(fire, this);
        pm.registerEvents(new ProtectionBlocks(), this);
        pm.registerEvents(new TickCounter(), this);
        pm.registerEvents(entityTracker, this);
        pm.registerEvents(magicSand, this);
        pm.registerEvents(multiDispenser, this);
        pm.registerEvents(new WaterproofRedstone(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
