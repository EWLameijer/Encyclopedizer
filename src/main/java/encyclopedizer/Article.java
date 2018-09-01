package encyclopedizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric-Wubbo on 08-07-17.
 */
public class Article implements Comparable<Article> {
    private String topic;
    private String description;

    /**
     * Constructor. If entry does not contain ":", it is considered invalid, yielding an
     * empty article (topic and description both "")
     *
     * @param entry the entry, like "Java[programming]: C/C++-like programming language, designed to run anywhere"
     */
    Article(String entry) {
        int separatorPos = entry.indexOf(':');
        if (separatorPos < 0) {
            ReportError.report("Skipping badly formatted entry " + entry);
            topic = "";
            description = "";
            return;
        }
        topic= entry.substring(0,separatorPos);
        description = entry.substring(separatorPos+1).trim();
    }

    /**
     * returns the topic of the article, for example if the article is "Java[programming]: C/C++-like programming
     * language, designed to run anywhere", it returns "Java".
     *
     * @return the topic of this article
     */
    public String getTopic() {
        return topic;
    }

    public Article cleanUp() {
        String newTopic = "";
        for (int i = 0; i < topic.length(); i++) {
            char ch = topic.charAt(i);
            if (ch != '\n' && ch != ':' && ch !='\t' && ch != '\r') newTopic += ch;
        }
        topic = newTopic;
        String newDescription = "";
        for (int i = 0; i < description.length(); i++) {
            char ch = description.charAt(i);
            if (ch != '\n' && ch !='\t' && ch != '\r') newDescription += ch;
        }
        description = newDescription;
        return this;
    }

    /**
     * Returns the description part of the article, for example if the article is "Java[programming]: C/C++-like programming
     * language, designed to run anywhere", it returns "C/C++-like programming language, designed to run anywhere".
     *
     * @return the description part of the article.
     */
    public String getDescription() {
        return description;
    }

    // support Comparable<Article>, to allow the articles to be sorted by the Encyclopedia object
    public int compareTo(Article otherArticle) {
        int caseIndependentCompare = topic.compareToIgnoreCase(otherArticle.topic);
        if (caseIndependentCompare != 0) {
            return caseIndependentCompare;
        } else {
            return topic.compareTo(otherArticle.topic);
        }
    }

    public static Article defaultArticle = new Article(":");

    @Override public String toString() {
        return topic + ": " + description;
    }

    public void addToDescription(String addedDescription) {
        description += addedDescription;
    }
}
