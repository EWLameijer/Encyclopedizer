package encyclopedizer;

/**
 * Created by Eric-Wubbo on 08-07-17.
 */
public class Entry {
    private String name;
    private String description;

    Entry(String entry) {
        int separatorPos = entry.indexOf(':');
        name = entry.substring(0,separatorPos);
        description = entry.substring(separatorPos+1);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
