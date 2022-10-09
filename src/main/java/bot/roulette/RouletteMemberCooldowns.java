package bot.roulette;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RouletteMemberCooldowns {
    private static RouletteMemberCooldowns cooldowns;
    private final HashMap<String, Integer> data;

    private final String FILE_PATH;
    public final int COOLDOWN_TIME;

    private RouletteMemberCooldowns() {
        Dotenv cfg = Dotenv.configure().load();
        FILE_PATH = Objects.requireNonNull(cfg.get("COOLDOWNS_FILE_PATH", "src/main/resources/roulette_member_cooldowns.txt"));
        COOLDOWN_TIME = Integer.parseInt(Objects.requireNonNull(cfg.get("COOLDOWN_TIME", "120")));

        data = new HashMap<>();

        if(!new File(FILE_PATH).exists())
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String userTag;
            while ((userTag = reader.readLine()) != null) {
                String cooldown = reader.readLine();
                data.put(userTag, Integer.valueOf(cooldown));
            }
            System.out.println("RouletteMemberCooldowns successfully cached");
        } catch (IOException e) {
            throw new RouletteException(e.getMessage());
        } catch (NumberFormatException e) {
            throw new RouletteException("Invalid roulette_member_cooldowns data");
        }
    }

    static RouletteMemberCooldowns getInstance() {
        if(cooldowns == null)
            cooldowns = new RouletteMemberCooldowns();

        return cooldowns;
    }

    private int currentTime() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public synchronized boolean isEligibleToPlay(String userId) {
        return (currentTime() - data.getOrDefault(userId, 0)) >= COOLDOWN_TIME;
    }

    public synchronized void setMemberCooldown(String userId) {
        data.put(userId, currentTime());
    }

    public synchronized int getCooldownByUserId(String userId) {
        if(data.containsKey(userId))
            return COOLDOWN_TIME - (currentTime() - data.get(userId));
        return 0;
    }

    synchronized void backUp() {
        try {
            ensureFileExists();

            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
            for(Map.Entry<String, Integer> entry : data.entrySet()) {
                writer.write(entry.getKey() + System.lineSeparator());
                writer.write(entry.getValue() + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void ensureFileExists() throws IOException {
        if(!new File(FILE_PATH).exists()) {
            boolean success = new File(FILE_PATH).createNewFile();

            if(!success)
                throw new RouletteException("Failed to create \"roulette_member_cooldowns.txt\" file");
        }
    }
}
