package org.ldcgc.backend.util.process;

public class Process {

    public static void runInBackground(Runnable runnable) {
        new Thread(runnable).start();
    }

}
