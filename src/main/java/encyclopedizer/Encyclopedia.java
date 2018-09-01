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

    private List<Article> articles = new ArrayList<>();
    private static final String DEFAULT_ENCY_FILENAME =  "testency.txt";
    private final String filename;

    public Encyclopedia(String encyFilename) {
        if (encyFilename.equals("")) {
            encyFilename = DEFAULT_ENCY_FILENAME;
        }
        filename = encyFilename;
        try {
            final Path path = Paths.get(encyFilename);
            final Charset charset = StandardCharsets.UTF_8;
            List<String> lines = Files.readAllLines(path, charset);
            lines.stream().filter(line -> !line.isEmpty()).map(Article::new).forEach(this::loadEntry);
            Collections.sort(articles);
            checkForDuplicates();
        } catch (IOException e) {
            System.out.println("Encylopedia::Encyclopedia(String): IOException occurred");
        }
    }

    private void checkForDuplicates() {
        // as I am possibly merging articles, cannot use simple foreach loop here...
        for (int entryIndex = 1; entryIndex < articles.size(); entryIndex++) {
            Article currentArticle = articles.get(entryIndex);
            Article previousArticle = articles.get(entryIndex - 1);
            if (currentArticle.getTopic().equals(previousArticle.getTopic())) {
                ReportError.report("Duplicate in names: " + currentArticle.getTopic());
                previousArticle.addToDescription(currentArticle.getDescription());
                articles.remove(entryIndex);
                entryIndex--; // to handle the possibility of multiple duplicates (triplicates and such)
            }
        }
    }

    private void saveEncy() {
        Path path = Paths.get(filename);
        List<String> lines = new ArrayList<>();

        for (Article article : articles) {
            lines.add(article.toString());
            lines.add(""); // newline, so things become more readable
        }
        try {
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e){
            ReportError.report("Problems writing to encyclopedia.");
        }
    }

    public void replaceEntry(Article originalArticle, Article newArticle) {
        articles.remove(originalArticle);
        addEntry(newArticle);
    }

    /**
     * adds a new entry to the encyclopedia, and takes care that is is saved immediately
     *
     * @param newArticle the entry to be added
     */
    public void addEntry(Article newArticle) {
        articles.add(newArticle);
        Collections.sort(articles);
        saveEncy();
    }

    private void loadEntry(Article newArticle) {
        articles.add(newArticle);
    }

    /**
     * Returns whether a certain string (the 'searchedString') starts with another string
     * (the 'soughtString'). Ignores case.
     *
     * @param searchedString
     * @param soughtString
     * @return
     */
    private boolean startsWithIgnoreCase(String searchedString, String soughtString) {
        if (soughtString.length() > searchedString.length()) return false;
        return soughtString.equalsIgnoreCase(searchedString.substring(0,soughtString.length()));
    }

    public Optional<Article> getEntryStartingWith(String textStart) {
        for (Article article : articles) {
            if (startsWithIgnoreCase(article.getTopic(),textStart)) return Optional.of(article);
        }
        return Optional.empty();
    }

    public Article getNextEntry(Article article) {
        int index = articles.indexOf(article);
        if (index < (articles.size() - 1)) index++;
        return articles.get(index);
    }

    public Article getPreviousEntry(Article article) {
        int index = articles.indexOf(article);
        if (index >= 1) index--;
        return articles.get(index);
    }
}
