public class TestServer implements Runnable {
    private Thread thread;
    private final String rootDirectory;

    public TestServer(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    // TODO: Wait for server to start
    public void start() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void stop() {
        this.thread.interrupt();
    }

    @Override
    public void run() {
        Main.main(new String[]{this.rootDirectory});
    }
}
