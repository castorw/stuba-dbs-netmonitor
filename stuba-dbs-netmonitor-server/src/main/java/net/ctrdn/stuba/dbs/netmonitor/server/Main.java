package net.ctrdn.stuba.dbs.netmonitor.server;

public class Main {

    public static void main(String[] args) {
        ServerDaemon serverDaemon = new ServerDaemon();
        new Thread(serverDaemon).start();
    }
}
