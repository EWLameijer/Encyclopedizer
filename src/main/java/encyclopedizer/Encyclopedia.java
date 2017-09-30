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
    private List<String> categories = new ArrayList<>();
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
            lines.stream().filter(line -> !line.isEmpty()).filter(line -> line.startsWith("[")).map(this::stripBrackets).forEach(this::addCategory);
            lines.stream().filter(line -> !line.isEmpty()).filter(line -> !line.startsWith("[")).map(Entry::new).forEach(this::loadEntry);
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

    private String stripBrackets(String originalText) {
        if (originalText.startsWith("[") && originalText.endsWith("]")) {
            return originalText.substring(1,originalText.length()-1);
        } else {
            return originalText;
        }
    }

    private void checkForDuplicates() {
        if (entries.size() == 0) {
            return;
        }
        // as I am possibly merging entries, cannot use simple foreach loop here...
        for (int entryIndex = 1; entryIndex < entries.size(); entryIndex++) {
            Entry currentEntry = entries.get(entryIndex);
            Entry previousEntry = entries.get(entryIndex - 1);
            if (currentEntry.getTopic().equals(previousEntry.getTopic())) {
                System.out.println("Duplicate in names: " + currentEntry.getTopic());
                previousEntry.addToDescription(currentEntry.getDescription());
                entries.remove(entryIndex);
                entryIndex--; // to handle multiple possible duplicates
            }
        }
    }

    private void saveEncy() {
        Path path = Paths.get(filename);
        List<String> lines = new ArrayList<>();
        for (String category: categories) {
            lines.add("[" + category + "]");
        }
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
            if (entry.getTopic().startsWith(textStart)) return Optional.of(entry);
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

    private String normalizeCategory(String rawCategoryName) {
        return rawCategoryName.trim().toLowerCase();
    }

    public boolean containsCategory(String categoryName) {
        return categories.contains(normalizeCategory(categoryName));
    }

    public void addCategory(String categoryName) {
        String normalizedName = normalizeCategory(categoryName);
        if (containsCategory(categoryName)) {
            throw new RuntimeException("Category already exists!");
        } else {
            categories.add(normalizedName);
            Collections.sort(categories);
        }
    }

    public Optional<String> getFirstCategory(String firstCharacter) {
        for (String category: categories) {
            if (category.startsWith(firstCharacter)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }

    public void updateCategories(Entry entry, String newCategories) {
        entry.updateCategories(newCategories);
        saveEncy();
    }
}
