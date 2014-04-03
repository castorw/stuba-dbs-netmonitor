package net.ctrdn.stuba.dbs.netmonitor.server.snmp;

import java.io.IOException;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.SnmpException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpClient {

    private Snmp snmp;
    private TransportMapping snmpTransportMapping;
    private NmDevice deviceRecord;

    public SnmpClient(NmDevice device) throws SnmpException {
        try {
            this.deviceRecord = device;
            this.snmpTransportMapping = new DefaultUdpTransportMapping();
            this.snmp = new Snmp(this.snmpTransportMapping);
            this.snmpTransportMapping.listen();
        } catch (IOException ex) {
            SnmpException finalEx = new SnmpException("SNMP configuration failed");
            finalEx.addSuppressed(ex);
            throw finalEx;
        }
    }

    public String getSnmpDataAsString(OID oid) throws SnmpException {
        ResponseEvent snmpResponse = this.getSnmpData(new OID[]{oid});
        if (snmpResponse.getResponse() == null) {
            throw new SnmpException("SNMP peer " + this.deviceRecord.getDeviceName() + " (" + this.deviceRecord.getIpv4Address() + ") did not respond to OID request " + oid.toString());
        }
        return snmpResponse.getResponse().get(0).getVariable().toString();
    }

    public ResponseEvent getSnmpData(OID oids[]) throws SnmpException {
        try {
            PDU pdu = new PDU();
            for (OID oid : oids) {
                pdu.add(new VariableBinding(oid));
            }
            pdu.setType(PDU.GET);
            ResponseEvent event = this.snmp.send(pdu, this.getSnmpTarget(), null);
            if (event != null) {
                return event;
            }
            throw new SnmpException("SNMP Get Timed Out");
        } catch (IOException ex) {
            SnmpException finalEx = new SnmpException("SNMP Get Failed");
            finalEx.addSuppressed(ex);
            throw finalEx;
        }
    }

    private Target getSnmpTarget() {
        Address targetAddress = GenericAddress.parse("udp:" + this.deviceRecord.getIpv4Address() + "/161");
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("stuba-netmonitor"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }
}
