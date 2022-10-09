package bot;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Entry {
    public static void main(String[] args) throws LoginException, IOException, InterruptedException {
        new Bot();

        //aaa();
    }

    private static void aaa() throws InterruptedException {
        Storage storage = new Storage();
        Thread threadA = new Thread(new A(storage));
        Thread threadB = new Thread(new B (new Storage()));

        threadA.start();
        Thread.sleep(1);
        threadB.start();
    }
}

class A implements Runnable {
    Storage storage;

    A(Storage storage) {
        this.storage = storage;
    }


    @Override
    public void run() {
            try {
                storage.incA();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}

class B implements Runnable {

    Storage storage;

    B(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
            try {
                storage.showA();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}

class Storage {
    int a = 1;

    public synchronized void incA() throws InterruptedException {
        System.out.println("Incrementing..." + " please wait");
        Thread.sleep(3000);
        a++;
    }

    public synchronized void showA() throws InterruptedException {
        System.out.println(a);
    }
}
