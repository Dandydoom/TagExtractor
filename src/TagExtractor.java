import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Reads a text file and counts how often each word appears, skipping stop words.
 * The words that are left are the tags (keywords) for the document.
 *
 * @author Kirby Fortney
 */
public class TagExtractor
{
    private TreeSet<String> stopWords = new TreeSet<>();
    private TreeMap<String, Integer> tags = new TreeMap<>();

    /**
     * Loads the stop words from a text file with one word per line.
     *
     * @param file the stop words file
     * @throws IOException if the file cannot be read
     */
    public void loadStopWords(File file) throws IOException
    {
        stopWords.clear();
        try (Scanner in = new Scanner(file, "UTF-8"))
        {
            while (in.hasNextLine())
            {
                String word = in.nextLine().trim().toLowerCase();
                if (!word.isEmpty())
                {
                    stopWords.add(word);
                }
            }
        }
    }

    /**
     * Scans a text file and counts every word that is not a stop word.
     * Non letter characters are removed and words are made lowercase.
     *
     * @param file the text file to scan
     * @throws IOException if the file cannot be read
     */
    public void extractTags(File file) throws IOException
    {
        tags.clear();
        try (Scanner in = new Scanner(file, "UTF-8"))
        {
            while (in.hasNext())
            {
                String word = in.next().replaceAll("[^a-zA-Z]", "").toLowerCase();

                if (!word.isEmpty() && !stopWords.contains(word))
                {
                    tags.put(word, tags.getOrDefault(word, 0) + 1);
                }
            }
        }
    }

    /**
     * Gets the number of stop words that are loaded.
     *
     * @return the stop word count
     */
    public int getStopWordCount()
    {
        return stopWords.size();
    }

    /**
     * Gets the number of different tags found.
     *
     * @return the tag count
     */
    public int getTagCount()
    {
        return tags.size();
    }

    /**
     * Formats the tags and their frequencies, most frequent first.
     * Tags with the same frequency stay in alphabetical order.
     *
     * @return one tag and its frequency per line
     */
    public String formatTags()
    {
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(tags.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : list)
        {
            result.append(String.format("%-20s %6d%n", entry.getKey(), entry.getValue()));
        }
        return result.toString();
    }

    /**
     * Saves the tags and their frequencies to a text file.
     *
     * @param file the file to write
     * @throws IOException if the file cannot be written
     */
    public void saveTags(File file) throws IOException
    {
        try (PrintWriter out = new PrintWriter(file, "UTF-8"))
        {
            out.print(formatTags());
        }
    }
}
