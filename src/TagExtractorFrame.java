import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * GUI for the Tag Extractor. The user picks a text file and a stop words file,
 * extracts the tags, and can save them to a file.
 *
 * @author Kirby Fortney
 */
public class TagExtractorFrame extends JFrame
{
    private TagExtractor extractor = new TagExtractor();
    private File textFile = null;
    private File stopWordsFile = null;
    private boolean tagsReady = false;

    private JLabel textFileLabel;
    private JLabel stopWordsLabel;
    private JLabel extractingLabel;
    private JTextArea tagArea;

    public TagExtractorFrame()
    {
        setTitle("Tag Extractor");
        setLayout(new BorderLayout(5, 5));

        add(createFilePanel(), BorderLayout.NORTH);
        add(createTagPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel createFilePanel()
    {
        JPanel filePanel = new JPanel(new GridLayout(3, 2, 5, 5));
        filePanel.setBorder(BorderFactory.createTitledBorder("Files"));

        JButton textFileButton = new JButton("Choose Text File");
        JButton stopWordsButton = new JButton("Choose Stop Words File");
        JButton extractButton = new JButton("Extract Tags");

        textFileLabel = new JLabel("No file chosen");
        stopWordsLabel = new JLabel("No file chosen");

        textFileButton.addActionListener(ae -> chooseTextFile());
        stopWordsButton.addActionListener(ae -> chooseStopWordsFile());
        extractButton.addActionListener(ae -> extractTags());

        filePanel.add(textFileButton);
        filePanel.add(textFileLabel);
        filePanel.add(stopWordsButton);
        filePanel.add(stopWordsLabel);
        filePanel.add(extractButton);
        filePanel.add(new JLabel());
        return filePanel;
    }

    private JPanel createTagPanel()
    {
        JPanel tagPanel = new JPanel(new BorderLayout());
        tagPanel.setBorder(BorderFactory.createTitledBorder("Tags and Frequencies"));

        extractingLabel = new JLabel("Extracting tags from: (none)");
        extractingLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        tagArea = new JTextArea(20, 40);
        tagArea.setEditable(false);
        tagArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        tagPanel.add(extractingLabel, BorderLayout.NORTH);
        tagPanel.add(new JScrollPane(tagArea), BorderLayout.CENTER);
        return tagPanel;
    }

    private JPanel createButtonPanel()
    {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JButton saveButton = new JButton("Save Tags");
        JButton quitButton = new JButton("Quit");

        saveButton.addActionListener(ae -> saveTags());
        quitButton.addActionListener(ae -> quit());

        buttonPanel.add(saveButton);
        buttonPanel.add(quitButton);
        return buttonPanel;
    }

    /**
     * Opens a file chooser in the project folder and returns the chosen file.
     *
     * @return the file, or null if the user cancelled
     */
    private File pickFile()
    {
        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private void chooseTextFile()
    {
        File file = pickFile();
        if (file != null)
        {
            textFile = file;
            textFileLabel.setText(file.getName());
        }
    }

    private void chooseStopWordsFile()
    {
        File file = pickFile();
        if (file != null)
        {
            stopWordsFile = file;
            stopWordsLabel.setText(file.getName());
        }
    }

    /**
     * Loads the stop words, scans the text file and shows the tags.
     */
    private void extractTags()
    {
        if (textFile == null || stopWordsFile == null)
        {
            JOptionPane.showMessageDialog(this, "Please choose a text file and a stop words file first.",
                    "Missing File", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try
        {
            extractor.loadStopWords(stopWordsFile);
            extractor.extractTags(textFile);
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, "Could not read the file: " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        extractingLabel.setText("Extracting tags from: " + textFile.getName()
                + "   (" + extractor.getTagCount() + " tags)");
        tagArea.setText(extractor.formatTags());
        tagArea.setCaretPosition(0); // scroll back to the top
        tagsReady = true;
    }

    /**
     * Lets the user pick where to save the tags and writes them to that file.
     */
    private void saveTags()
    {
        if (!tagsReady)
        {
            JOptionPane.showMessageDialog(this, "Extract the tags before saving.",
                    "Nothing to Save", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        chooser.setSelectedFile(new File("tags.txt"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            File file = chooser.getSelectedFile();
            try
            {
                extractor.saveTags(file);
                JOptionPane.showMessageDialog(this, "Tags saved to " + file.getName());
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(this, "Could not save the file: " + e.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void quit()
    {
        int answer = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?",
                "Quit", JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION)
        {
            System.exit(0);
        }
    }
}
