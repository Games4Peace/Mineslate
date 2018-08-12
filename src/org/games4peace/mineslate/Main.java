package org.games4peace.mineslate;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.games4peace.mineslate.translation.YandexTranslator;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        // Initiate default config
        saveDefaultConfig();
        MineSlateConfig.initConfig(this.getConfig());
        saveConfig();

        // Set PrivateChatHandler to handle chat messages
        getServer().getPluginManager().registerEvents(
                new GroupChatHandler(new YandexTranslator()), this);
    }
}
