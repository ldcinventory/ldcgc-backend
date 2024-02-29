package org.ldcgc.backend.util.process;

public class Threads {

    public static void runInBackground(Runnable runnable) {
        new Thread(runnable).start();
    }

}
