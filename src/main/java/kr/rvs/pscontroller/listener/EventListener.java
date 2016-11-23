package kr.rvs.pscontroller.listener;

import kr.rvs.pscontroller.handler.ConfigHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;

/**
 * Created by deide on 2016-11-22.
 */
public class EventListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ProtocolVersion version = ProtocolSupportAPI.getProtocolVersion(e.getPlayer());
        if (!ConfigHandler.isAllow(version)) {
            e.getPlayer().kickPlayer(ConfigHandler.getKickMsgTitle() + "\n" +
                    version.getName() + " §fversion is not allowed");
        }
    }
}
