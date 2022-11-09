package config;

import java.util.LinkedHashMap;

import utils.Utils;

/**
 * A pair (key, value) in configuration file setting.properties
 *
 * @author ducanh
 */
public class Attributes extends LinkedHashMap<String, String> implements IAttributes {

    private static final long serialVersionUID = 1L;

    @Override
    public String put(String key, int value) {
        return super.put(key, Utils.normalizePath(value + ""));
    }
}
