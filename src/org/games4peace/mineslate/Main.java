package org.games4peace.mineslate;

import org.bukkit.plugin.java.JavaPlugin;
import org.games4peace.mineslate.translation.YandexTranslator;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        // Initiate default config
        saveDefaultConfig();
        MineslateConfig.initConfig(this.getConfig());
        saveConfig();

        // Set PrivateChatHandler to handle chat messages
        getServer().getPluginManager().registerEvents(
                new GroupChatHandler(new YandexTranslator()), this);
    }
}
