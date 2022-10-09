package bot.roulette;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.*;
import java.util.Objects;

public class Star {
    private final String FILE_PATH;
    private final int AUTO_RESET_TIME;
    private static Star star;
    private String starId;
    private int number;
    private int assignmentTimestamp;
    private boolean isStarPresent = false;
    private String previousStarId;

    static Star getInstance() {
        if(star == null)
            star = new Star();

        return star;
    }

    private Star() {
        Dotenv cfg = Dotenv.configure().load();
        FILE_PATH = Objects.requireNonNull(cfg.get("STAR_FILE_PATH"));
        AUTO_RESET_TIME = Integer.parseInt(Objects.requireNonNull(cfg.get("STAR_AUTO_RESET_TIME")));

        if(!new File(FILE_PATH).exists())
            return;

        try(BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {

            String retrievedStarId = reader.readLine();
            if(retrievedStarId != null) {
                String numberStr = reader.readLine();
                String assignmentTimestampStr = reader.readLine();

                starId = retrievedStarId;
                number = Integer.parseInt(numberStr);
                assignmentTimestamp = Integer.parseInt(assignmentTimestampStr);
                isStarPresent = true;

                System.out.println("Star successfully cached");
            }
        } catch (IOException e) {
            throw new RouletteException(e.getMessage());
        } catch (NumberFormatException e) {
            throw new RouletteException("Invalid star data");
        }
    }

    public synchronized void resetStar() {
        if(getTimeLeft() > 0)
            return;

        previousStarId = starId;
        assignmentTimestamp = 0;
        number = 0;
        starId = null;
        isStarPresent = false;
    }

    public synchronized boolean amIStar(String userId) {
        return isStarPresent() && getStarId().equals(userId);
    }

    public synchronized boolean ifNewStar(int candidateNumber, String candidateUserId) {
        if(isStarPresent() && (starId.equals(candidateUserId) || candidateNumber < number))
            return false;

        previousStarId = starId;
        starId = candidateUserId;
        number = candidateNumber;
        assignmentTimestamp = currentTime();
        isStarPresent = true;
        return true;
    }
    public synchronized boolean isStarPresent() {
        return isStarPresent;
    }

    public synchronized String getStarId() {
        return starId;
    }

    public synchronized int getStarNumber() {
        return number;
    }

    public synchronized int getAssignmentTimestamp() {
        return assignmentTimestamp;
    }

    public synchronized int getTimeLeft() {
        int timeLeft = AUTO_RESET_TIME - (currentTime() - assignmentTimestamp);
        return Math.max(timeLeft, 0);
    }

    public static int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public synchronized String getPreviousStarId() {
        return previousStarId;
    }

    synchronized void backUp() {
        if(!isStarPresent())
            return;
        try {
            ensureFileExists();
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));

            writer.write(starId + System.lineSeparator());
            writer.write(number + System.lineSeparator());
            writer.write(assignmentTimestamp + "");

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void ensureFileExists() throws IOException {
        if(!new File(FILE_PATH).exists()) {
            boolean success = new File(FILE_PATH).createNewFile();

            if(!success)
                throw new RouletteException("Failed to create \"star.txt\" file");
        }
    }
}
