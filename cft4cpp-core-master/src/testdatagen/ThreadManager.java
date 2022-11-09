package testdatagen;

import java.util.ArrayList;

import tree.object.Node;

public class ThreadManager extends ArrayList<TestdataGenerationThread> {
    /**
     *
     */
    private static final long serialVersionUID = -8879353007415826270L;
    /**
     * Singleton pattern
     */
    private static ThreadManager instance = null;
    boolean runNow = false;

    private ThreadManager() {

        Thread tt = new Thread(() -> {
            try {
                while (true)
                    if (ThreadManager.this.size() > 0) {
                        Thread latestThread = ThreadManager.this.get(0);
                        latestThread.start();
                        latestThread.join();
                        ThreadManager.this.remove(0);
                    } else
                        Thread.sleep(1000);
            } catch (Exception e) {

            }
        });
        tt.start();
    }

    public static ThreadManager getInstance() {
        if (ThreadManager.instance == null)
            ThreadManager.instance = new ThreadManager();
        return ThreadManager.instance;
    }

    public boolean add(Thread t) {
        return super.add((TestdataGenerationThread) t);
    }

    public boolean remove(Node fn) {
        for (TestdataGenerationThread t : this)
            if (t.getFunctionNode().equals(fn)) {
                t.interrupt();
                t.stop();
                this.remove(t);
                break;
            }
        return true;
    }

    public boolean resume(Node fn) {
        for (TestdataGenerationThread t : this)
            if (t.getFunctionNode().equals(fn)) {
                t.resume();
                break;
            }
        return true;
    }

    public boolean stop(Node fn) {
        for (TestdataGenerationThread t : this)
            if (t.getFunctionNode().equals(fn)) {
                t.suspend();
                break;
            }
        return true;
    }
}
