package bot.cache;

public class Backupper implements Runnable {
    private final int INTERVAL;
    private boolean isActive = false;
    private boolean isStarted = false;

    public Backupper(int interval) {
        if(interval < 60)
            throw new RuntimeException("Too small interval");

        this.INTERVAL = interval;
    }

    @Override
    public void run() {
        try {
            start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void start() throws InterruptedException {
        if(isStarted)
            return;

        isStarted = true;
        isActive = true;

        while (isActive) {
            Thread.sleep(INTERVAL * 1000L);
            RouletteMemberCooldowns.backUp();
            System.out.println("RouletteMemberCooldowns successfully backed up");
            Star.backUp();
            System.out.println("Star successfully backed up");
        }
    }

    private void stop() {
        if(isActive)
            isActive = false;
    }
}
