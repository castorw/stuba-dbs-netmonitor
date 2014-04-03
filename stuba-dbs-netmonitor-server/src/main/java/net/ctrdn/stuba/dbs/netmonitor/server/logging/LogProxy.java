package net.ctrdn.stuba.dbs.netmonitor.server.logging;

public interface LogProxy {

    public void message(LogSeverity severity, String message);

    public void exception(Throwable thrwbl);
}
