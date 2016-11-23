package kr.rvs.pscontroller;

import kr.rvs.pscontroller.handler.ConfigHandler;
import kr.rvs.pscontroller.listener.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

/**
 * Created by deide on 2016-11-22.
 */
public class ProtocolSupportController extends JavaPlugin {
    @Override
    public void onEnable() {
        ConfigHandler.init(getDataFolder());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            saveDefaultConfig();
            ConfigHandler.reload();
            for (Player p : Bukkit.getOnlinePlayers()) {
                ProtocolVersion version = ProtocolSupportAPI.getProtocolVersion(p);
                if (!ConfigHandler.isAllow(version)) {
                    p.kickPlayer(ConfigHandler.getKickMsgTitle() + "\n" +
                            version.getName() + " §fversion is not allowed");
                }
            }
            sender.sendMessage("Reloaded");
        } else {
            sender.sendMessage("Permission denied");
        }
        return true;
    }
}
