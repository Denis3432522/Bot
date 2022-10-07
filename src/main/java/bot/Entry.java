package bot;

import bot.cache.Backupper;
import bot.cache.RouletteMemberCooldowns;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import unused.Controller;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Entry {
    public static void main(String[] args) throws LoginException, IOException, InterruptedException {
        new Bot();
//        Backupper backupper = new Backupper(60*1);
//
//        Thread thread = new Thread(backupper);
//        thread.start();
//
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    }

}

class A implements Runnable {

    private static void aaa() throws InterruptedException {
        Thread threadA = new Thread(new A());
        Thread threadB = new Thread(new B ());

        threadA.start();
        Thread.sleep(1);
        threadB.start();
    }

    @Override
    public void run() {
            try {
                Storage.incA();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}

class B implements Runnable {

    @Override
    public void run() {
            try {
                Storage.showA();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}

class Storage {
    static int a = 1;

    public synchronized static void incA() throws InterruptedException {
        System.out.println("Incrementing..." + " please wait");
        Thread.sleep(3000);
        a++;
    }

    public synchronized static void showA() throws InterruptedException {
        System.out.println(a);
    }
}
