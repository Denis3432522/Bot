package bot.roulette;

public class Backupper {
    private final int INTERVAL;
    private boolean isActive = false;
    private int lastBackupTime = (int) (System.currentTimeMillis() / 1000);

    public Backupper(int interval) {
        if(interval < 60)
            throw new RuntimeException("Too small interval");

        this.INTERVAL = interval;
    }

    public void check() {
        int currentTime = (int) (System.currentTimeMillis() / 1000);

        if(currentTime - lastBackupTime > INTERVAL) {
            lastBackupTime = currentTime;
            backUp();
        }
    }

    private void backUp() {
        RouletteMemberCooldowns.getInstance().backUp();
        System.out.println("RouletteMemberCooldowns successfully backed up");
        Star.getInstance().backUp();
        System.out.println("Star successfully backed up");
    }

    private void stop() {
        if(isActive)
            isActive = false;
    }
}
