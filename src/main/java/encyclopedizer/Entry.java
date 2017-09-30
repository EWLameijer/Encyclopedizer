package encyclopedizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric-Wubbo on 08-07-17.
 */
public class Entry implements Comparable<Entry> {
    private String term;
    private String description;
    private List<String> categories = new ArrayList<>();

    Entry(String entry) {
        int separatorPos = entry.indexOf(':');
        String termWithCategories = entry.substring(0,separatorPos);
        int bracketPos = termWithCategories.indexOf('[');
        if (bracketPos < 0) { // bracket not found, so no categories
            term = termWithCategories;
        } else {
            term = termWithCategories.substring(0,bracketPos);
            while (bracketPos > 0) {
                int endOfCategory = termWithCategories.indexOf(']',bracketPos);
                categories.add(termWithCategories.substring(bracketPos + 1, endOfCategory ));
                bracketPos = termWithCategories.indexOf('[',endOfCategory + 1);
            }
        }

        description = entry.substring(separatorPos+1).trim();
    }

    public String getTopic() {
        return term;
    }

    public String getDescription() {
        return description;
    }

    public void updateCategories(String newCategoriesConcatenated) {
        int index = 0;
        int lengthOfString = newCategoriesConcatenated.length();
        while (index < lengthOfString) {
            int endOfCurrentCategory = newCategoriesConcatenated.indexOf(']',index );
            categories.add(newCategoriesConcatenated.substring(index + 1, endOfCurrentCategory ));
            index = endOfCurrentCategory + 1;
            System.out.println(toLine());
        }
    }

    public int compareTo(Entry otherEntry) {
        return term.compareTo(otherEntry.term);
    }

    public static Entry DEFAULT_ENTRY = new Entry(":");

    public String toLine() {

        StringBuilder result = new StringBuilder(term);
        for (String category: categories) {
            result.append("[" + category + "]");
        }
        result.append( ": " + description);
        return result.toString();
    }

    public void addToDescription(String addedDescription) {
        description += addedDescription;
    }

    public String getCategoriesAsString() {
        StringBuilder result = new StringBuilder();
        for (String category : categories) {
            result.append("[" + category + "]");
        }
        return result.toString();
    }
}
