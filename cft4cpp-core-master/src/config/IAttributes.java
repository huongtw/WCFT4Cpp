package config;

/**
 * A pair (key, value) in configuration file setting.properties
 *
 * @author ducanh
 */
public interface IAttributes {
    /**
     * Add new (key, value) to the setting
     *
     * @param key
     * @param value
     * @return
     */
    String put(String key, int value);

}