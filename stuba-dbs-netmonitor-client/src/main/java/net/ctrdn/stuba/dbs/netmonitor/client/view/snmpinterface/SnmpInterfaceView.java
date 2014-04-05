package net.ctrdn.stuba.dbs.netmonitor.client.view.snmpinterface;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.Client;
import net.ctrdn.stuba.dbs.netmonitor.client.annotation.NetmonitorView;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmInterface;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmInterfaceAggregatedStats;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@NetmonitorView(
        author = "Lubomir Kaplan",
        name = "SNMP Interface Stats View",
        description = "View for interface statistics and aggregated per-device statistics",
        version = "1.0.0",
        displayName = "SNMP Interface Stats",
        orderKey = 220
)
public class SnmpInterfaceView implements View {

    protected interface SnmpDeviceContainer {

        public NmDevice getDevice();

        public NmProbe getProbe();
    }

    private final Client client;
    private final SnmpInterfacePanel panel;
    private Session mysqlSession = null;

    public SnmpInterfaceView(Client client) {
        this.client = client;
        this.panel = new SnmpInterfacePanel(this);
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
        if (this.mysqlSession != null) {
            this.mysqlSession.close();
        }
        this.mysqlSession = this.getClient().getMysqlSessionFactory().openSession();
        this.panel.refresh();
    }

    public List<NmDevice> getDeviceList() {
        this.mysqlSession.beginTransaction();
        List<NmDevice> deviceList = new CopyOnWriteArrayList<>(this.mysqlSession.createCriteria(NmDevice.class).list());
        for (NmDevice deviceRecord : deviceList) {
            boolean probeFound = false;
            for (NmProbe probeRecord : (Set<NmProbe>) deviceRecord.getNmProbes()) {
                if (probeRecord.getNmProbeType().getProbeClasspath().equals("net.ctrdn.stuba.dbs.netmonitor.server.probe.iface.SnmpInterfaceProbe")) {
                    probeFound = true;
                    break;
                }
            }
            if (!probeFound) {
                deviceList.remove(deviceRecord);
            }
        }
        this.mysqlSession.getTransaction().commit();
        return deviceList;
    }

    protected SnmpDeviceContainer getDevice(int id) {
        this.mysqlSession.beginTransaction();
        NmDevice deviceRecord = (NmDevice) this.mysqlSession.load(NmDevice.class, id);
        this.mysqlSession.merge(deviceRecord);
        this.mysqlSession.evict(deviceRecord);
        deviceRecord = (NmDevice) this.mysqlSession.load(NmDevice.class, id);
        final NmDevice fDeviceRecord = deviceRecord;
        NmProbe probe = null;
        for (NmProbe probeRecord : (Set<NmProbe>) deviceRecord.getNmProbes()) {
            if (probeRecord.getNmProbeType().getProbeClasspath().equals("net.ctrdn.stuba.dbs.netmonitor.server.probe.iface.SnmpInterfaceProbe")) {
                probe = probeRecord;
                break;
            }
        }
        final NmProbe fProbeRecord = probe;
        this.mysqlSession.getTransaction().commit();
        return new SnmpDeviceContainer() {

            @Override
            public NmDevice getDevice() {
                return fDeviceRecord;
            }

            @Override
            public NmProbe getProbe() {
                return fProbeRecord;
            }
        };
    }

    protected NmInterfaceAggregatedStats getAggregatedStats(NmProbe probeRecord) {
        this.mysqlSession.beginTransaction();
        NmInterfaceAggregatedStats statsRecord = (NmInterfaceAggregatedStats) this.mysqlSession.createCriteria(NmInterfaceAggregatedStats.class).add(Restrictions.eq("nmProbe", probeRecord)).addOrder(Order.desc("createDate")).setMaxResults(1).uniqueResult();
        this.mysqlSession.getTransaction().commit();
        return statsRecord;
    }

    protected NmInterface getInterface(int id) {
        this.mysqlSession.beginTransaction();
        NmInterface interfaceRecord = (NmInterface) this.mysqlSession.load(NmInterface.class, id);
        this.mysqlSession.merge(interfaceRecord);
        this.mysqlSession.evict(interfaceRecord);
        interfaceRecord = (NmInterface) this.mysqlSession.load(NmInterface.class, id);
        this.mysqlSession.getTransaction().commit();
        return interfaceRecord;
    }

    public static String getReadableByteSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
