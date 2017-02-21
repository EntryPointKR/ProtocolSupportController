package kr.rvs.pscontroller.listener;

import kr.rvs.pscontroller.handler.ConfigHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.events.PlayerSyncLoginEvent;

/**
 * Created by deide on 2016-11-22.
 */
public class EventListener implements Listener {
    @EventHandler
    public void onLogin(PlayerSyncLoginEvent e) {
        ProtocolVersion version = e.getConnection().getVersion();
        if (!ConfigHandler.isAllow(version)) {
            e.denyLogin(ConfigHandler.getKickMsgTitle() + "\n" +
                    version.getName() + " §fversion is not allowed");
        }
    }
}
