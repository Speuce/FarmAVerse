package main.java.com.speuce.farmtopia.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardWrapper {
	 
    public static final int MAX_LINES = 16;
 
    private final Scoreboard scoreboard;
    private final Objective objective;
 
    private final List<String> modifies = new ArrayList<>(MAX_LINES);
 
    /**
     * Instantiate a new ScoreboardWrapper with a default title.
     */
    public ScoreboardWrapper(String title) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("dummy", title);
        objective.setDisplayName(title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
 
    /**
     * Set the scoreboard title.
     */
    public void setTitle(String title) {
        objective.setDisplayName(title);
    }
 
    private String getLineCoded(String line) {
        String result = line;
        while (modifies.contains(result)) {
            result += ChatColor.RESET;
        }
        return result.substring(0, Math.min(40, result.length()));
    }
 
    /**
     * Add a new line to the scoreboard. Throw an error if the lines count are higher than 16.
     */
    public void addLine(String line) {
        if (modifies.size() > MAX_LINES)
            throw new IndexOutOfBoundsException("You cannot add more than 16 lines.");
        String modified = getLineCoded(line);
        modifies.add(modified);
        objective.getScore(modified).setScore(16-(modifies.size()));
    }
 
    /**
     * Add a blank space to the scoreboard.
     */
    public void addBlankSpace() {
        addLine(" ");
    }
 
    /**
     * Set a scoreboard line to an exact index (between 0 and 15).
     */
    public void setLine(int index, String line) {
        if (index < 0 || index >= MAX_LINES)
            throw new IndexOutOfBoundsException("The index cannot be negative or higher than 15.");
        String oldModified = modifies.get(index);
        scoreboard.resetScores(oldModified);
        String modified = getLineCoded(line);
        modifies.set(index, modified);
        objective.getScore(modified).setScore(16-(index + 1));
    }
 
    /**
     * Get the Bukkit Scoreboard.
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
 
    /**
     * Just for debug.
     */
    @Override
    public String toString() {
        String out = "";
        int i = 0;
        for (String string : modifies)
            out += 16-(i + 1) + ") -> " + string + ";\n";
        return out;
    }
}