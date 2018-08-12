package org.games4peace.mineslate.translation;

import org.games4peace.mineslate.MineslateConfig;

import java.util.HashMap;
import java.util.Map;

public abstract class Translator {
    private Map<String, Map<String, String>> translationCache;

    public Translator() {
        translationCache = MineslateConfig.getLocalTranslationDict();
    }

    public String translate(String msg, String receivingPlayerName) {
        String translation = null;

        String language = MineslateConfig.getLanguageForUsername(receivingPlayerName);
        if(language != null) {
            translation = cacheTranslate(msg, language);
            if(translation == null) {
                translation = onlineTranslate(msg, language);

                addTranslationToCache(msg, translation, language);
            }
        }

        return translation;
    }

    private void addTranslationToCache(String msg, String translation, String language) {
        Map<String, String> dict = translationCache.get(language);
        if(dict == null) {
            dict = new HashMap<>();
        }

        dict.put(msg, translation);
        translationCache.put(language, dict);
    }

    private String cacheTranslate(String msg, String language) {
        String cachedTranslation = null;

        Map<String, String> dict = translationCache.get(language);
        if(dict != null) {
            cachedTranslation = dict.get(msg);
        }

        return  cachedTranslation;
    }

    protected abstract String onlineTranslate(String msg, String language);
}
