package net.ctrdn.stuba.dbs.netmonitor.client;

import java.util.Properties;
import javax.swing.JFrame;
import net.ctrdn.stuba.dbs.netmonitor.client.view.MainFrame;
import org.hibernate.SessionFactory;

public interface Client {

    public MainFrame getApplicationMainFrame();

    public SessionFactory getMysqlSessionFactory();

    public Properties getConfiguration();

    public void start();

    public void handleException(Throwable thrwbl);

    public String getApplicationName();

    public String getApplicationVersion();
}
