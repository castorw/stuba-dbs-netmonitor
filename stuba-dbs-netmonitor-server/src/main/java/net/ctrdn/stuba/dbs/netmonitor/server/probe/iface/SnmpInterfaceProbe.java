package net.ctrdn.stuba.dbs.netmonitor.server.probe.iface;

import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;
import net.ctrdn.stuba.dbs.netmonitor.server.Server;
import net.ctrdn.stuba.dbs.netmonitor.server.annotation.NetmonitorProbe;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.ProbeException;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.SnmpException;
import net.ctrdn.stuba.dbs.netmonitor.server.probe.Probe;
import net.ctrdn.stuba.dbs.netmonitor.server.snmp.SnmpClient;

@NetmonitorProbe(
        author = "Lubomir Kaplan",
        name = "SNMP Interface Probe",
        description = "Remote device interface stats probe",
        version = "1.0.0"
)
public class SnmpInterfaceProbe implements Probe {

    private Server server;
    private NmProbe probeRecord;
    private NmDevice deviceRecord;
    private SnmpClient snmpClient;

    @Override
    public void configure(Server server, NmProbe probeRecord, NmDevice deviceRecord) throws ProbeException {
        try {
            this.server = server;
            this.deviceRecord = deviceRecord;
            this.probeRecord = probeRecord;
            this.snmpClient = new SnmpClient(deviceRecord);
        } catch (SnmpException ex) {
            ProbeException finalEx = new ProbeException("Failed to initialize SNMP client");
            finalEx.addSuppressed(ex);
            throw finalEx;
        }
    }

    @Override
    public void poll() throws ProbeException {
    }

}
