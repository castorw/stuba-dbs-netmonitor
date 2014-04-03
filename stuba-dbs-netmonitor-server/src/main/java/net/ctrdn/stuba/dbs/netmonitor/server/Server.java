package net.ctrdn.stuba.dbs.netmonitor.server;

import java.util.Properties;
import net.ctrdn.stuba.dbs.netmonitor.server.logging.LogProxy;
import net.ctrdn.stuba.dbs.netmonitor.server.probe.Probe;
import org.hibernate.SessionFactory;

public interface Server {

    public SessionFactory getMysqlSessionFactory();

    public Properties getConfiguration();

    public LogProxy getLog();

    public Probe newProbe(String classpath);
}
