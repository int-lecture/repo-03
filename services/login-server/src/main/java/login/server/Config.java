package login.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Config {
    baseURI("http://0.0.0.0:5001/"),
    mongoURI("mongodb://141.19.142.57:27017"),
    allowEmailLogin("true");

    private static Map<String, List<String>> settings = new HashMap<>();

    private List<String> defaultValue;

    Config(String defaultValue) {
        this.defaultValue = new ArrayList<>();
        this.defaultValue.add(defaultValue);
    }

    Config(String... defaultValues) {
        this.defaultValue = new ArrayList<>(defaultValues.length);
        for (String value :
                defaultValues) {
            this.defaultValue.add(value);
        }

    }

    public static void init(String[] args) throws Exception {
        String currentKey = null;
        ArrayList<String> values = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                currentKey = arg.substring(1);
            } else {
                if (currentKey == null && i == args.length - 1) {
                  insertNewSettings(loadConfigFile(arg),false);
                } else if (currentKey == null) {
                    throw new Exception("Found value without defined key!");
                }
                else {
                    values.add(arg);
                    settings.put(currentKey, values);
                    values = new ArrayList<>();
                    currentKey = null;
                }
            }
        }

        for (Config defaultVal :
                Config.values()) {
            if (!settings.containsKey(defaultVal.name())) {
                settings.put(defaultVal.name(), defaultVal.defaultValue);
            }
        }
    }

    public static String getSettingValue(Config key) {
        return settings.get(key.name()).get(0);
    }

    public static List<String> getSettingValues(Config key) {
        return new ArrayList<>(settings.get(key.name()));
    }

    private static Map<String, List<String>> loadConfigFile(String configJson) {
        String jsonStr = "";
        try {
            jsonStr = new String(Files.readAllBytes(Paths.get(configJson)));
        } catch (IOException e) {
            System.out.printf("Could not open config file %s. Caused by %s\n", configJson, e.getMessage());
        }

        JSONObject json = new JSONObject(jsonStr);
        Map<String, List<String>> settings = new HashMap<>();
        for (String key :
                json.keySet()) {
            List<String> values = new ArrayList<>();
            JSONArray valArray = json.optJSONArray(key);
            String valString = json.optString(key);
            if (valArray != null) {
                valArray.forEach((o -> values.add((String) o)));
            } else if (valString != null) {
                values.add(valString);
            }

            settings.put(key, values);
        }

        return settings;
    }

    private static void insertNewSettings(Map<String, List<String>> settings, boolean overrideExisting) {
        for (String key :
                settings.keySet()) {
            if (!Config.settings.containsKey(key) || overrideExisting) {
                Config.settings.put(key, settings.get(key));
            }
        }
    }
}
