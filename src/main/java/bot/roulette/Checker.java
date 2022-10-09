package bot.roulette;

public class Checker implements Runnable {
    private final Backupper backupper;
    private final RouletteGame rouletteGame;
    private boolean isStarted = false;
    private final int INTERVAL;

    public Checker(Backupper backupper, RouletteGame rouletteGame, int INTERVAL) {
        if(INTERVAL < 10)
            throw new RouletteException("check interval value is too small");

        this.backupper = backupper;
        this.rouletteGame = rouletteGame;
        this.INTERVAL = INTERVAL;
    }

    public void run() {
        if(isStarted)
            return;
        isStarted = true;

        while (true) {
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            backupper.check();
            rouletteGame.checkStarTimeLeft();
        }
    }
}
