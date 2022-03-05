package stanuwu.fragmentcore2.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import stanuwu.fragmentcore2.FragmentCore2;
import stanuwu.fragmentcore2.helpers.Helper;

public class JoinEvent implements Listener {
    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        if (FragmentCore2.config.getBoolean("fragmentcore.cosmetic.enable-automated-messages")) {
            event.setJoinMessage("");
            Player player = event.getPlayer();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Helper.ParsePrefix(Helper.getConfigString("cosmetic", "join-message").replaceAll("%player%", player.getDisplayName()))));
            for(Player online_players : Bukkit.getOnlinePlayers()){
                online_players.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.ParsePrefix(Helper.getConfigString("cosmetic", "global-join-message").replaceAll("%player%", player.getDisplayName()))));
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (FragmentCore2.config.getBoolean("enable-automated-messages")) {
            Player player = event.getPlayer();
            for (Player online_players : Bukkit.getOnlinePlayers()) {
                online_players.sendMessage(ChatColor.translateAlternateColorCodes('&',Helper.ParsePrefix(Helper.getConfigString("cosmetic", "global-leave-message").replaceAll("%player%", player.getDisplayName()))));
            }
            event.setQuitMessage("");
        }
    }
}
