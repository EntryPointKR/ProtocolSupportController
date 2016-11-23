package kr.rvs.pscontroller.handler;

import kr.rvs.pscontroller.container.RemapData;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.api.remapper.BlockRemapperControl;
import protocolsupport.api.remapper.ItemRemapperControl;
import protocolsupport.utils.ProtocolVersionsHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by deide on 2016-11-22.
 */
public class ConfigHandler {
    private static final String CONNECTION_SEP = "connection.";
    private static final String KICK_TITLE = "kick-title";
    private static final String BLOCK_REMAP = "block-remap";
    private static final String ITEM_REMAP = "item-remap";
    private static File parentFile;
    private static FileConfiguration config;

    public static void init(File parentFile) {
        ConfigHandler.parentFile = parentFile;
        File configFile = getConfigFile();
        FileConfiguration config;
        if (configFile.isFile()) {
            config = YamlConfiguration.loadConfiguration(configFile);
            // Config checking
            for (ProtocolVersion version : ProtocolVersionsHelper.ALL) {
                String versionStr = CONNECTION_SEP + getVersionString(version);
                if (!config.contains(versionStr)) {
                    config.set(versionStr, true);
                }
            }
        } else {
            config = new YamlConfiguration();
            // Connection
            for (ProtocolVersion version : ProtocolVersionsHelper.ALL) {
                config.set(CONNECTION_SEP + getVersionString(version), true);
            }
            // Kick title
            config.set(KICK_TITLE, getDefaultKickMsgTitle());
            // Block remap
            config.set(BLOCK_REMAP, getDefaultBlockRemapData());
            // Item remap
            config.set(ITEM_REMAP, getDefaultBlockRemapData());
        }
        ConfigHandler.config = config;
        setRemapData();
        try {
            config.save(configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(getConfigFile());
        setRemapData();
    }

    public static String getDefaultKickMsgTitle() {
        return "&c[ ProtocolSupportController ]&f";
    }

    public static String getKickMsgTitle() {
        String msg = config.getString(KICK_TITLE);
        if (msg != null) {
            return ChatColor.translateAlternateColorCodes('&', msg);
        }
        return null;
    }

    public static boolean isAllow(ProtocolVersion version) {
        return config.getBoolean(CONNECTION_SEP + getVersionString(version));
    }

    @SuppressWarnings("deprecation")
    private static List<RemapData> getRemapData(boolean isBlock) {
        List<RemapData> ret = new ArrayList<>();
        List<String> valueList;
        if (isBlock) {
            valueList = config.getStringList(BLOCK_REMAP);
        } else {
            valueList = config.getStringList(ITEM_REMAP);
        }
        for (String value : valueList) {
            String[] fromTo = value.split("\\|");
            BlockRemapperControl.MaterialAndData fromMatData = null;
            BlockRemapperControl.MaterialAndData toMatData = null;
            int i = 0;
            for (String element : fromTo) {
                String[] matDataElements = element.split(":");
                String matStr = matDataElements[0];
                Material material;
                int data = 0;
                if (NumberUtils.isNumber(matStr)) {
                    material = Material.getMaterial(NumberUtils.toInt(matStr));
                } else {
                    material = Material.getMaterial(matStr.toUpperCase());
                }
                if (matDataElements.length >= 2) {
                    String dataStr = matDataElements[1];
                    if (NumberUtils.isNumber(dataStr)) {
                        data = NumberUtils.toInt(dataStr);
                    }
                }
                if (material != null) {
                    BlockRemapperControl.MaterialAndData matAndData = new BlockRemapperControl.MaterialAndData(material, data);
                    if (i++ == 0) {
                        fromMatData = matAndData;
                    } else {
                        toMatData = matAndData;
                    }
                }
            }
            if (fromMatData != null && toMatData != null) {
                ret.add(new RemapData(fromMatData, toMatData));
            }
        }
        return ret;
    }

    private static void setRemapData(boolean isBlock) {
        List<RemapData> remapDataList = getRemapData(isBlock);
        for (ProtocolVersion version : ProtocolVersionsHelper.ALL) {
            if (isBlock) {
                BlockRemapperControl control = ProtocolSupportAPI.getBlockRemapper(version);
                for (RemapData remapData : remapDataList) {
                    control.setRemap(remapData.getFrom(), remapData.getTo());
                }
            } else {
                ItemRemapperControl control = ProtocolSupportAPI.getItemRemapper(version);
                for (RemapData remapData : remapDataList) {
                    control.setRemap(remapData.getFrom().getMaterial(), remapData.getTo().getMaterial());
                }
            }
        }
    }

    private static void setRemapData() {
        setRemapData(true);
        setRemapData(false);
    }

    private static List<String> getDefaultBlockRemapData() {
        return Arrays.asList("35:5|1", "35:6|2");
    }

    private static String getVersionString(ProtocolVersion version) {
        return version.getName().replace(".", "_");
    }

    private static File getConfigFile() {
        return new File(parentFile, "config.yml");
    }
}
