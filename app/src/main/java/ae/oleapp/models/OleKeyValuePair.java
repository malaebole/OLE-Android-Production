package ae.oleapp.models;

public class OleKeyValuePair {
    private final String key;
    private final String value;
    public OleKeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
