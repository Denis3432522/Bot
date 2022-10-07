package bot.cache;

import java.io.*;
import java.util.Map;

public class Star {
    private static String starTag;
    private static int number;
    private static int assignmentTimestamp;
    private static final String FILE_PATH = "src/main/resources/star.txt";
    private static final int AUTO_RESET_TIME = 2*60; //24*60*60;
    private static boolean isStarPresent = false;
    private static String previousStarTag;

    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));

            String retrievedStarTag = reader.readLine();
            if(retrievedStarTag != null) {
                String numberStr = reader.readLine();
                String assignmentTimestampStr = reader.readLine();

                starTag = retrievedStarTag;
                number = Integer.parseInt(numberStr);
                assignmentTimestamp = Integer.parseInt(assignmentTimestampStr);
                isStarPresent = true;

                System.out.println("Star successfully cached");
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid star data");
        }

    }

    public synchronized static boolean ifNewStar(int candidateNumber, String candidateUserTag) {
        if(isStarPresent() && candidateNumber < number)
            return false;

        starTag = candidateUserTag;
        number = candidateNumber;
        assignmentTimestamp = (int) (System.currentTimeMillis() / 1000);
        isStarPresent = true;
        return true;
    }

    public synchronized static boolean isStarPresent() {
        if(isStarPresent && getTimeLeft() == 0) {
            previousStarTag = starTag;
            isStarPresent = false;
        }

        return isStarPresent;
    }

    public synchronized static String getStarTag() {
        return starTag;
    }

    public synchronized static int getStarNumber() {
        return number;
    }

    public synchronized static int getAssignmentTimestamp() {
        return assignmentTimestamp;
    }

    public synchronized static int getTimeLeft() {
        int timeLeft = AUTO_RESET_TIME - (currentTime() - assignmentTimestamp);
        return Math.max(timeLeft, 0);
    }

    public static int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public synchronized static String getPreviousStarTag() {
        return previousStarTag;
    }

    static synchronized void backUp() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {

            writer.write(starTag + "\r\n");
            writer.write(number + "\r\n");
            writer.write(assignmentTimestamp + "");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
