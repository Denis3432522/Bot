package bot.cache;

import bot.Entry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class RouletteMemberCooldowns {
    public static HashMap<String, Integer> data;
    private static final String filePath = "src/main/resources/roulette_member_cooldowns.txt";
    public static final int COOLDOWN = 60*1;

    static {
        data = new HashMap<>();
        int buffSize = (int) new File(filePath).length();
        if(buffSize > 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath), buffSize)) {
                String userTag;
                while ((userTag = reader.readLine()) != null) {
                    String cooldown = reader.readLine();
                    data.put(userTag, Integer.valueOf(cooldown));
                }

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid data");
            }
        }
        System.out.println("RouletteMemberCooldowns successfully cached");
    }

    private RouletteMemberCooldowns() {}

    private synchronized static int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public synchronized static boolean isEligibleToPlay(String userTag) {
        int currentTimestamp = currentTime();

        if(!data.containsKey(userTag)) {
            data.put(userTag, currentTimestamp);
            return true;
        }

        long memberTimestamp = data.get(userTag);

        if((currentTimestamp - memberTimestamp) < COOLDOWN)
            return false;

        data.put(userTag, currentTimestamp);
        return true;
    }

    public synchronized static int getCooldownByName(String name) {
        int temp = currentTime();
        if(data.containsKey(name))
            return COOLDOWN - (temp - data.get(name));
        return 0;
    }

    static synchronized void backUp() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath), 8_192)) {

            for(Map.Entry<String, Integer> entry : data.entrySet()) {
                writer.write(entry.getKey() + "\r\n");
                writer.write(entry.getValue() + "\r\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
