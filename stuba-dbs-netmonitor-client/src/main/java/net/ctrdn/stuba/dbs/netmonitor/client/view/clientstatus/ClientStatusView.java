package net.ctrdn.stuba.dbs.netmonitor.client.view.clientstatus;

import javax.swing.JPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.Client;
import net.ctrdn.stuba.dbs.netmonitor.client.annotation.NetmonitorView;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmInterface;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmInterfaceAggregatedStats;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmInterfaceStats;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbeType;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

@NetmonitorView(
        author = "Lubomir Kaplan",
        name = "Client Status View",
        description = "Basic connection and database capacity information",
        version = "1.0.0",
        displayName = "System Status",
        orderKey = 5
)
public class ClientStatusView implements View {

    protected interface SystemStats {

        public String getApplicationName();

        public String getApplicationVersion();

        public String getDatabaseHostname();

        public String getDatabaseName();

        public long getDeviceCount();

        public long getInterfaceCount();

        public long getProbeTypeCount();

        public long getProbeCount();

        public long getInterfaceStatsCount();

        public long getInterfaceAggregatedStatsCount();
    }

    private final Client client;
    private final ClientStatusPanel panel;

    public ClientStatusView(Client client) {
        this.client = client;
        this.panel = new ClientStatusPanel(this);
    }

    @Override
    public JPanel getPanel() {
        return this.panel;
    }

    @Override
    public Client getClient() {
        return this.client;
    }

    @Override
    public void refresh() {
        this.panel.refresh();
    }

    protected SystemStats getSystemStats() {
        Session mysqlSession = this.getClient().getMysqlSessionFactory().openSession();
        mysqlSession.beginTransaction();

        final String applicationName = this.getClient().getApplicationName();
        final String applicationVersion = this.getClient().getApplicationVersion();
        final String databaseHostname = this.getClient().getConfiguration().getProperty("database.mysql.host");
        final String databaseName = this.getClient().getConfiguration().getProperty("database.mysql.database");
        Number deviceCountNumber = (Number) mysqlSession.createCriteria(NmDevice.class).setProjection(Projections.rowCount()).uniqueResult();
        final long deviceCount = deviceCountNumber.longValue();
        Number interfaceCountNumber = (Number) mysqlSession.createCriteria(NmInterface.class).setProjection(Projections.rowCount()).uniqueResult();
        final long interfaceCount = interfaceCountNumber.longValue();
        Number probeTypeCountNumber = (Number) mysqlSession.createCriteria(NmProbeType.class).setProjection(Projections.rowCount()).uniqueResult();
        final long probeTypeCount = probeTypeCountNumber.longValue();
        Number probeCountNumber = (Number) mysqlSession.createCriteria(NmProbe.class).setProjection(Projections.rowCount()).uniqueResult();
        final long probeCount = probeCountNumber.longValue();
        Number interfaceStatsCountNumber = (Number) mysqlSession.createCriteria(NmInterfaceStats.class).setProjection(Projections.rowCount()).uniqueResult();
        final long interfaceStatsCount = interfaceStatsCountNumber.longValue();
        Number interfaceAggregatedStatsCoutnNumber = (Number) mysqlSession.createCriteria(NmInterfaceAggregatedStats.class).setProjection(Projections.rowCount()).uniqueResult();
        final long interfaceAggregatedStatsCount = interfaceAggregatedStatsCoutnNumber.longValue();

        mysqlSession.getTransaction().commit();
        mysqlSession.disconnect();

        SystemStats stats = new SystemStats() {

            @Override
            public String getApplicationName() {
                return applicationName;
            }

            @Override
            public String getApplicationVersion() {
                return applicationVersion;
            }

            @Override
            public String getDatabaseHostname() {
                return databaseHostname;
            }

            @Override
            public String getDatabaseName() {
                return databaseName;
            }

            @Override
            public long getDeviceCount() {
                return deviceCount;
            }

            @Override
            public long getInterfaceCount() {
                return interfaceCount;
            }

            @Override
            public long getProbeTypeCount() {
                return probeTypeCount;
            }

            @Override
            public long getProbeCount() {
                return probeCount;
            }

            @Override
            public long getInterfaceStatsCount() {
                return interfaceStatsCount;
            }

            @Override
            public long getInterfaceAggregatedStatsCount() {
                return interfaceAggregatedStatsCount;
            }
        };

        return stats;
    }

}
