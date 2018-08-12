package org.games4peace.mineslate.translation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.games4peace.mineslate.MineslateConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;

public class YandexTranslator extends Translator {
    private static final String CONFIG_API_KEYS = "yandex_api_keys";

    private final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private final String API_KEY_PARAM = "key";
    private final String LANGUAGE_PARAM = "lang";
    private final String TEXT_PARAM = "text";

    private Collection<String> _apiKeys;
    private Collection<String> _badApiKeys;

    public YandexTranslator() {
        _apiKeys = new HashSet<>();
        _badApiKeys = new HashSet<>();

        Object apiKeysObj = MineslateConfig.getByPath(CONFIG_API_KEYS);
        try {
            _apiKeys = (Collection<String>) apiKeysObj;
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Malformed Config!", e);
        }
    }

    @Override
    protected String onlineTranslate(String msg, String language) {
        String translatedMsg = msg;

        int tries = 5;
        boolean success = false;
        while (!success && tries-- > 0) {
            String apiKey = getApiKey();
            if (apiKey != null) {
                String queryString = buildQueryString(apiKey, language, msg);
                if (queryString.length() > 0) {
                    StringBuilder url = new StringBuilder(BASE_URL).append("?").append(queryString);
                    String responseMsg = getTranslation(url.toString());
                    if (responseMsg != null) {
                        translatedMsg = responseMsg;
                        success = true;
                    } else {
                        badApiKey(apiKey);
                    }
                }
            }
        }

        return translatedMsg;
    }

    private String getApiKey() {
        if(_apiKeys.size() > 0)
            return _apiKeys.iterator().next();
        else
            return null;
    }
    private void badApiKey(String apiKey) {
        if(_apiKeys.size() < 2) {
            while (_badApiKeys.size() > 0) {
                String key = _badApiKeys.iterator().next();
                _badApiKeys.remove(key);
                _apiKeys.add(key);
            }
        } else {
            _apiKeys.remove(apiKey);
            _badApiKeys.add(apiKey);
        }
    }

    private String buildQueryString(String apiKey, String language, String msg) {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put(API_KEY_PARAM, apiKey);
        urlParams.put(LANGUAGE_PARAM, language);
        urlParams.put(TEXT_PARAM, msg);

        String queryString = "";
        try {
            StringBuilder builder = new StringBuilder();
            for (String key : urlParams.keySet()) {
                builder.append(key).append("=").append(URLEncoder.encode(urlParams.get(key), "UTF-8"));
                builder.append("&");
            }
            builder.deleteCharAt(builder.length() - 1);

            queryString = builder.toString();
        } catch (UnsupportedEncodingException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Translator URL encoding problem!", e);
        }

        return queryString;
    }
    private String getTranslation(String apiUrl) {
        Bukkit.getLogger().info("Accessing Yandex API with");

        String translation = null;

        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();

            JsonObject jsonObject = new JsonParser().parse(result.toString()).getAsJsonObject();
            translation = jsonObject.get("text").getAsString();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE,
                    "Error accessing translation api! URL: " + apiUrl + " | response: " + result.toString(), e);
        }

        return translation;
    }

}
