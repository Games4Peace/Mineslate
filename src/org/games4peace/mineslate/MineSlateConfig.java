package org.games4peace.mineslate;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Level;

public class MineslateConfig {
    private static final String GROUPS_LANGUAGE_SETTINGS = "groups_language_settings";
    private static final String GROUP_PRIORITY = "group_priority";
    private static final String GROUP_USERNAME_REGEX = "username_regex";
    private static final String GROUP_LANGUAGE = "group_language";

    private static final String LOCAL_TRANSLATION_DICT = "local_translation_dict";

    private static FileConfiguration _config;

    private MineslateConfig(){}

    public static void initConfig(FileConfiguration config){
        _config = config;
        _config.options().copyDefaults(true);
    }

    public static String getLanguageForUsername(String username) {
        String language = null;

        try {
            int priority = 0;
            Collection<Object> groupsLanguageSettings =
                    _config.getConfigurationSection(GROUPS_LANGUAGE_SETTINGS).getValues(false).values();
            for(Object singleGroupObj : groupsLanguageSettings) {
                MemorySection section = (MemorySection) singleGroupObj;
                int newPriority = Integer.parseInt(section.get(GROUP_PRIORITY).toString());
                if (newPriority > priority && username.matches(section.get(GROUP_USERNAME_REGEX).toString())) {
                    priority = newPriority;
                    language = section.get(GROUP_LANGUAGE).toString();
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Configuration malformed!", e);
        }

        return language;
    }

    public static Map<String, Map<String, String>> getLocalTranslationDict() {
        Map<String, Map<String, String>> localTranslationDisct = new HashMap<>();

        ConfigurationSection dictsConfigSection = _config.getConfigurationSection(LOCAL_TRANSLATION_DICT);
        if(dictsConfigSection != null) {
            try {
                Map<String, Object> translationsDicts = dictsConfigSection.getValues(false);
                for (String language : translationsDicts.keySet()) {
                    MemorySection section = (MemorySection)translationsDicts.get(language);

                    Map<String, String> dict = new HashMap<>();
                    for(String key : section.getKeys(false)) {
                        dict.put(key, section.get(key).toString());
                    }

                    localTranslationDisct.put(language, dict);
                }
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.SEVERE, "Configuration malformed!", e);
            }
        }

        return localTranslationDisct;
    }

    public static Object getByPath(String path) {
        return _config.get(path);
    }
}
