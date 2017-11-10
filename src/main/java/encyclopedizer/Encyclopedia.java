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
    private List<String> categories = new ArrayList<>();
    private static final String DEFAULT_ENCY_FILENAME =  "testency.txt";
    private String filename;
    public static final String ALL = "all";

    public Encyclopedia(String encyFilename) {
        if (encyFilename.equals("")) {
            encyFilename = DEFAULT_ENCY_FILENAME;
        }
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
            Collections.sort(articles);
            checkForDuplicates();
            System.out.println("Lines: " + lines);
            System.out.println("Number of articles: " + articles.size());
        } catch (IOException e) {
            System.out.println("Encylopedia::Encyclopedia(String): IOException occurred");
        }
    }

   // public Encyclopedia() {
        //this(DEFAULT_ENCY_FILENAME);
    //}

    private String stripBrackets(String originalText) {
        if (originalText.startsWith("[") && originalText.endsWith("]")) {
            return originalText.substring(1,originalText.length()-1);
        } else {
            return originalText;
        }
    }

    private void checkForDuplicates() {
        if (articles.size() == 0) {
            return;
        }
        // as I am possibly merging articles, cannot use simple foreach loop here...
        for (int entryIndex = 1; entryIndex < articles.size(); entryIndex++) {
            Article currentArticle = articles.get(entryIndex);
            Article previousArticle = articles.get(entryIndex - 1);
            if (currentArticle.getTopic().equals(previousArticle.getTopic())) {
                System.out.println("Duplicate in names: " + currentArticle.getTopic());
                previousArticle.addToDescription(currentArticle.getDescription());
                articles.remove(entryIndex);
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
        for (Article article : articles) {
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

    Optional<Article> getEntryStartingWith(String textStart) {
        for ( Article article : articles) {
            if (startsWithIgnoreCase(article.getTopic(),textStart)) return Optional.of(article);
        }
        return Optional.empty();
    }

    public String getFilename() {
        return filename;
    }

    public Article getNextEntry(Article article, String category) {
        int index = articles.indexOf(article);
        while (index < (articles.size() - 1)) {
            index++;
            Article nextArticle = articles.get(index);
            if (category.equals(ALL) ||nextArticle.hasCategory(category)) {
                return nextArticle;
            }
        }
        return article; // there is no 'next' article; don't respond to down button requests.

    }

    public Article getPreviousEntry(Article article, String category) {
        int index = articles.indexOf(article);
        while (index >= 1) {
            index--;
            Article previousArticle = articles.get(index);
            if (category.equals(ALL) || previousArticle.hasCategory(category)) {
                return previousArticle;
            }
        }
        return article; // you can't scroll in this direction

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
        Optional<Article> firstCorrectArticle = Optional.empty();
        for (Article article: articles) {
            if (article.getTopic().compareTo(currentTopic) >= 0 && article.hasCategory(desiredCategory)) {
                firstCorrectArticle = Optional.of(article);
                break;
            }
        }
        if (firstCorrectArticle.isPresent()) {
            return firstCorrectArticle.get();
        } else {
            // start loop from beginning
            for (Article article: articles) {
                if (article.hasCategory(desiredCategory)) {
                    return article;
                }
            }
        }
        // no article found at all ?!
        return new Article("[" + desiredCategory + "]:" );
    }
}
