public class TestServer implements Runnable {
    private Thread thread;
    private String rootDirectory;

    public TestServer(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    // TO-DO: Wait for server to start
    public void start() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void stop() {
        this.thread.interrupt();
    }

    @Override
    public void run() {
        FileServer.main(new String[]{this.rootDirectory});
    }
}
