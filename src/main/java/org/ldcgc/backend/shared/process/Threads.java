package org.ldcgc.backend.shared.process;

public class Threads {

    public static void runInBackground(Runnable runnable) {
        new Thread(runnable).start();
    }

}
