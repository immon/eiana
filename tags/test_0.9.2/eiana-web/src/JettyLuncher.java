import org.mortbay.jetty.Server;


public class JettyLuncher {


    private Server jettyServer;
    Thread shutDownHook;

    public JettyLuncher() {
        jettyServer = new Server();
    }

    public void start(int port) {
        try {
            jettyServer.addListener("0.0.0.0:" + port);
            jettyServer.addWebApplication("rzm", "./rzm/");
            jettyServer.start();

            shutDownHook = new Thread(new ShutDownHook(jettyServer));
            shutDownHook.setName("JettyServer Shutdown Hook");
            Runtime.getRuntime().addShutdownHook(shutDownHook);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {

        try {
            if (jettyServer.isStarted()) {
                jettyServer.stop(true);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ShutDownHook implements Runnable {
        Server jetty;

        ShutDownHook(Server jetty) {
            this.jetty = jetty;
        }

        public void run() {
            System.out.println("Shutting down...");
            try {
                if (jettyServer.isStarted()) {
                    jettyServer.stop(true);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
