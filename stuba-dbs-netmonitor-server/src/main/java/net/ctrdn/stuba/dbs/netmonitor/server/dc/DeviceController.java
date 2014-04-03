package net.ctrdn.stuba.dbs.netmonitor.server.dc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbeType;
import net.ctrdn.stuba.dbs.netmonitor.server.Server;
import net.ctrdn.stuba.dbs.netmonitor.server.annotation.NetmonitorProbe;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.ProbeException;
import net.ctrdn.stuba.dbs.netmonitor.server.logging.LogSeverity;
import net.ctrdn.stuba.dbs.netmonitor.server.probe.Probe;
import org.hibernate.Session;

public class DeviceController implements Runnable {

    private final Server server;
    private final NmDevice deviceRecord;
    private final int pollTime;
    private List<Probe> probeList;

    public DeviceController(Server server, NmDevice device) {
        this.server = server;
        this.deviceRecord = device;
        this.pollTime = Integer.parseInt(server.getConfiguration().getProperty("device.controller.polltime"));
    }

    @Override
    public void run() {
        this.probeList = new ArrayList<>();
        Session mysqlSession = this.server.getMysqlSessionFactory().openSession();
        mysqlSession.beginTransaction();
        List<NmProbe> dbProbeList = mysqlSession.createSQLQuery("SELECT * FROM `nm_probe` WHERE `device_id` = \"" + this.deviceRecord.getId() + "\"").addEntity(NmProbe.class).list();
        for (NmProbe probeRecord : dbProbeList) {
            try {
                NmProbeType probeTypeRecord = (NmProbeType) mysqlSession.createSQLQuery("SELECT * FROM `nm_probe_type` WHERE `id` = \"" + probeRecord.getNmProbeType().getId() + "\"").addEntity(NmProbeType.class).uniqueResult();
                Class<?> probeClass = Class.forName(probeTypeRecord.getProbeClasspath());
                Constructor probeConstructor = probeClass.getDeclaredConstructor();
                Probe probe = (Probe) probeConstructor.newInstance();
                probe.configure(this.server, probeRecord, this.deviceRecord);
                this.probeList.add(probe);
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException | ProbeException ex) {
                ex.printStackTrace();
            }
        }
        mysqlSession.getTransaction().commit();
        mysqlSession.disconnect();

        try {
            while (true) {
                for (Probe p : this.probeList) {
                    try {
                        Date pollStart = new Date();
                        p.poll();
                        Date pollEnd = new Date();
                        this.server.getLog().message(LogSeverity.NOTICE, "Probe " + p.getClass().getAnnotation(NetmonitorProbe.class).name() + " on " + this.deviceRecord.getDeviceName() + " took " + (pollEnd.getTime() - pollStart.getTime()) + "ms");
                    } catch (ProbeException ex) {
                        this.server.getLog().message(LogSeverity.ERROR, "Polling error: " + ex.getMessage());
                    }
                }
                Thread.sleep(Integer.parseInt(this.server.getConfiguration().getProperty("device.controller.polltime")) * 1000);
            }
        } catch (InterruptedException ex) {

        }
    }
}
