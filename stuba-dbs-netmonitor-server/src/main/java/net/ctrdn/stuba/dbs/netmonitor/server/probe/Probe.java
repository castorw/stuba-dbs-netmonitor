package net.ctrdn.stuba.dbs.netmonitor.server.probe;

import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;
import net.ctrdn.stuba.dbs.netmonitor.server.Server;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.ProbeException;

public interface Probe {

    public void configure(Server server, NmProbe probeRecord, NmDevice deviceRecord) throws ProbeException;

    public void poll() throws ProbeException;
}
