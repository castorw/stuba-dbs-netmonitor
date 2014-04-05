package net.ctrdn.stuba.dbs.netmonitor.client.view.device;

import java.util.Date;
import java.util.List;
import javax.swing.JPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.Client;
import net.ctrdn.stuba.dbs.netmonitor.client.annotation.NetmonitorView;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbeType;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

@NetmonitorView(
        author = "Lubomir Kaplan",
        name = "Device View",
        description = "Device table manipulation utilities",
        version = "1.0.0",
        displayName = "Devices",
        orderKey = 20
)
public class DeviceView implements View {

    private final Client client;
    private final DevicePanel panel;
    private Session mysqlSession = null;

    public DeviceView(Client client) {
        this.client = client;
        this.panel = new DevicePanel(this);
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

    protected List<NmDevice> getDeviceList() {
        this.mysqlSession.beginTransaction();
        List<NmDevice> deviceList = this.mysqlSession.createCriteria(NmDevice.class).addOrder(Order.asc("id")).list();
        this.mysqlSession.getTransaction().commit();
        return deviceList;
    }

    protected NmDevice getDevice(int id) {
        this.mysqlSession.beginTransaction();
        NmDevice deviceRecord = (NmDevice) this.mysqlSession.load(NmDevice.class, id);
        this.mysqlSession.merge(deviceRecord);
        this.mysqlSession.evict(deviceRecord);
        deviceRecord = (NmDevice) this.mysqlSession.load(NmDevice.class, id);
        this.mysqlSession.getTransaction().commit();
        return deviceRecord;
    }

    protected NmDevice updateDevice(NmDevice deviceRecord) {
        deviceRecord.setModifiedDate(new Date());
        this.mysqlSession.beginTransaction();
        this.mysqlSession.merge(deviceRecord);
        this.mysqlSession.getTransaction().commit();
        return this.getDevice(deviceRecord.getId());
    }

    protected NmDevice addDevice(NmDevice deviceRecord) {
        deviceRecord.setModifiedDate(new Date());
        this.mysqlSession.beginTransaction();
        this.mysqlSession.save(deviceRecord);
        this.mysqlSession.getTransaction().commit();
        return this.getDevice(deviceRecord.getId());
    }

    protected void deleteDevice(NmDevice deviceRecord) {
        this.mysqlSession.beginTransaction();
        this.mysqlSession.delete(deviceRecord);
        this.mysqlSession.getTransaction().commit();
    }

    protected List<NmProbeType> getProbeTypes() {
        this.mysqlSession.beginTransaction();
        List<NmProbeType> probeTypeList = this.mysqlSession.createCriteria(NmProbeType.class).addOrder(Order.asc("probeName")).list();
        this.mysqlSession.getTransaction().commit();
        return probeTypeList;
    }
}
