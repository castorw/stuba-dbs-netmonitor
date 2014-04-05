package net.ctrdn.stuba.dbs.netmonitor.client.view.probetype;

import java.util.List;
import javax.swing.JPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.Client;
import net.ctrdn.stuba.dbs.netmonitor.client.annotation.NetmonitorView;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbeType;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

@NetmonitorView(
        author = "Lubomir Kaplan",
        name = "Probe Type View",
        description = "List of loaded probes",
        version = "1.0.0",
        displayName = "Probe Types",
        orderKey = 1100
)
public class ProbeTypeView implements View {

    private final Client client;
    private final ProbeTypePanel panel;
    private Session mysqlSession = null;

    public ProbeTypeView(Client client) {
        this.client = client;
        this.panel = new ProbeTypePanel(this);
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

    protected List<NmProbeType> getProbeTypes() {
        this.mysqlSession.beginTransaction();
        List<NmProbeType> probeTypeList = this.mysqlSession.createCriteria(NmProbeType.class).addOrder(Order.asc("probeName")).list();
        this.mysqlSession.getTransaction().commit();
        return probeTypeList;
    }
}
