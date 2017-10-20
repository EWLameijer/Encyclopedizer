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

    private List<Article> entries = new ArrayList<>();
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
            lines.stream().filter(line -> !line.isEmpty()).filter(line -> !line.startsWith("[")).map(Article::new).forEach(this::loadEntry);
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
            Article currentArticle = entries.get(entryIndex);
            Article previousArticle = entries.get(entryIndex - 1);
            if (currentArticle.getTopic().equals(previousArticle.getTopic())) {
                System.out.println("Duplicate in names: " + currentArticle.getTopic());
                previousArticle.addToDescription(currentArticle.getDescription());
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
        for (Article article : entries) {
            lines.add(article.toLine());
            lines.add("");
        }
        try {
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e){
            System.out.println("Problems writing to encyclopedia.");
        }
    }

    public void replaceEntry(Article originalArticle, Article newArticle) {
        entries.remove(originalArticle);
        addEntry(newArticle);
    }

    /**
     * adds a new entry to the encyclopedia, and takes care that is is saved immediately
     *
     * @param newArticle the entry to be added
     */
    public void addEntry(Article newArticle) {
        entries.add(newArticle);
        Collections.sort(entries);
        saveEncy();
    }

    private void loadEntry(Article newArticle) {
        entries.add(newArticle);
    }

    Optional<Article> getEntryStartingWith(String textStart) {
        for ( Article article : entries ) {
            if (article.getTopic().startsWith(textStart)) return Optional.of(article);
        }
        return Optional.empty();
    }

    public String getFilename() {
        return filename;
    }

    public Article getNextEntry(Article article) {
        int index = entries.indexOf(article);
        if (index < (entries.size() - 1)) {
            return entries.get(index + 1);
        } else {
            return article;
        }

    }

    public Article getPreviousEntry(Article article) {
        int index = entries.indexOf(article);
        if (index >= 1) {
            return entries.get(index - 1);
        } else {
            return article;
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

    public void updateCategories(Article article, String newCategories) {
        article.updateCategories(newCategories);
        saveEncy();
    }

    public void getCategoryList(List<String> list) {
        list.add("all");
        for (String category: categories) {
            list.add(category);
        }
    }

    public Article getNextArticle(String currentTopic, String desiredCategory) {
        throw new RuntimeException("Not yet implemented");
        //return new Article(":");
    }
}
