package net.ctrdn.stuba.dbs.netmonitor.client;

public class Main {

    public static void main(String[] args) {
        Client client = new ClientImpl();
        client.start();
    }
}
