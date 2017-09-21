package encyclopedizer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by Eric-Wubbo on 09-07-17.
 */
public class Encyclopedia {

    private List<Entry> entries = new ArrayList<>();
    private static final String DEFAULT_ENCY_FILENAME =  "testency.txt";
    private String filename;

    public Encyclopedia(String encyFilename) {
        try {
            System.out.println("Started " + encyFilename);
            Path pwd = Paths.get("").toAbsolutePath();
            System.out.println("path=" + pwd);
            final Path path = Paths.get(encyFilename);
            filename = encyFilename;
            final Charset charset = StandardCharsets.UTF_8;
            List<String> lines = Files.readAllLines(path, charset);
            lines.stream().filter(line -> !line.isEmpty()).map(Entry::new).forEach(this::loadEntry);
            Collections.sort(entries);
            checkForDuplicates();
            System.out.println("Lines: " + lines);
            System.out.println("Number of entries: " + entries.size());
        } catch (IOException e) {
            System.out.println("Excepted");
        }
    }

    public Encyclopedia() {
        this(DEFAULT_ENCY_FILENAME);
    }

    private void checkForDuplicates() {
        if (entries.size() == 0) {
            return;
        }
        // as I am possibly merging entries, cannot use simple foreach loop here...
        for (int entryIndex = 1; entryIndex < entries.size(); entryIndex++) {
            Entry currentEntry = entries.get(entryIndex);
            Entry previousEntry = entries.get(entryIndex - 1);
            if (currentEntry.getName().equals(previousEntry.getName())) {
                System.out.println("Duplicate in names: " + currentEntry.getName());
                previousEntry.addToDescription(currentEntry.getDescription());
                entries.remove(entryIndex);
                entryIndex--; // to handle multiple possible duplicates
            }
        }
    }

    private void saveEncy() {
        Path path = Paths.get(filename);
        List<String> lines = new ArrayList<>();
        for (Entry entry : entries) {
            lines.add(entry.toLine());
            lines.add("");
        }
        try {
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e){
            System.out.println("Problems writing to encyclopedia.");
        }
    }

    public void replaceEntry(Entry originalEntry, Entry newEntry) {
        entries.remove(originalEntry);
        addEntry(newEntry);
    }

    /**
     * adds a new entry to the encyclopedia, and takes care that is is saved immediately
     *
     * @param newEntry the entry to be added
     */
    public void addEntry(Entry newEntry) {
        entries.add(newEntry);
        Collections.sort(entries);
        saveEncy();
    }

    private void loadEntry(Entry newEntry) {
        entries.add(newEntry);
    }

    Optional<Entry> getEntryStartingWith(String textStart) {
        for ( Entry entry : entries ) {
            if (entry.getName().startsWith(textStart)) return Optional.of(entry);
        }
        return Optional.empty();
    }

    public String getFilename() {
        return filename;
    }

    public Entry getNextEntry(Entry entry) {
        int index = entries.indexOf(entry);
        if (index < (entries.size() - 1)) {
            return entries.get(index + 1);
        } else {
            return entry;
        }

    }

    public Entry getPreviousEntry(Entry entry) {
        int index = entries.indexOf(entry);
        if (index >= 1) {
            return entries.get(index - 1);
        } else {
            return entry;
        }
    }


}
