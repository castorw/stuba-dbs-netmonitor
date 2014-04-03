package net.ctrdn.stuba.dbs.netmonitor.server.probe.iface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.ctrdn.stuba.dbs.netmonitor.server.exception.SnmpException;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public class SnmpInterfaceList<E extends SnmpInterface> extends ArrayList<E> implements List<E> {

    public final static OID ifTableOid = new OID("1.3.6.1.2.1.2.2");
    public final static OID ifIndexOid = new OID("1.3.6.1.2.1.2.2.1.1");
    public final static OID ifDescrOid = new OID("1.3.6.1.2.1.2.2.1.2");
    public final static OID ifTypeOid = new OID("1.3.6.1.2.1.2.2.1.3");
    public final static OID ifAdminStatusOid = new OID("1.3.6.1.2.1.2.2.1.7");
    public final static OID ifOperStatusOid = new OID("1.3.6.1.2.1.2.2.1.8");
    public final static OID ifRxBytesOid = new OID("1.3.6.1.2.1.2.2.1.10");
    public final static OID ifRxUcastPacketsOid = new OID("1.3.6.1.2.1.2.2.1.11");
    public final static OID ifRxNUcastPacketsOid = new OID("1.3.6.1.2.1.2.2.1.12");
    public final static OID ifRxDiscardsOid = new OID("1.3.6.1.2.1.2.2.1.13");
    public final static OID ifRxErrorsOid = new OID("1.3.6.1.2.1.2.2.1.14");
    public final static OID ifTxBytesOid = new OID("1.3.6.1.2.1.2.2.1.16");
    public final static OID ifTxUcastPacketsOid = new OID("1.3.6.1.2.1.2.2.1.17");
    public final static OID ifTxNUcastPacketsOid = new OID("1.3.6.1.2.1.2.2.1.18");
    public final static OID ifTxDiscardsOid = new OID("1.3.6.1.2.1.2.2.1.19");
    public final static OID ifTxErrorsOid = new OID("1.3.6.1.2.1.2.2.1.20");

    public static SnmpInterfaceList<SnmpInterface> buildList(List<VariableBinding> bindings) throws SnmpException {
        SnmpInterfaceList<SnmpInterface> list = new SnmpInterfaceList<>();
        List<Integer> indexList = new ArrayList<>();
        Map<Integer, Integer> indexMap = new HashMap<>();
        Map<Integer, String> nameMap = new HashMap<>();
        Map<Integer, String> typeMap = new HashMap<>();
        Map<Integer, Integer> adminStatusMap = new HashMap<>();
        Map<Integer, Integer> operStatusMap = new HashMap<>();
        Map<Integer, Long> rxBytesMap = new HashMap<>();
        Map<Integer, Long> txBytesMap = new HashMap<>();
        Map<Integer, Long> rxPacketsMap = new HashMap<>();
        Map<Integer, Long> txPacketsMap = new HashMap<>();
        Map<Integer, Long> rxDDMap = new HashMap<>();
        Map<Integer, Long> txDDMap = new HashMap<>();
        for (VariableBinding vb : bindings) {
            if (vb.getOid().startsWith(ifTableOid)) {
                OID currentOid = vb.getOid();
                int snmpIfIndex = currentOid.get(currentOid.size() - 1);
                if (currentOid.startsWith(ifIndexOid)) {
                    indexMap.put(snmpIfIndex, vb.getVariable().toInt());
                } else if (currentOid.startsWith(ifDescrOid)) {
                    nameMap.put(snmpIfIndex, vb.getVariable().toString());
                } else if (currentOid.startsWith(ifTypeOid)) {
                    typeMap.put(snmpIfIndex, vb.getVariable().toString());
                } else if (currentOid.startsWith(ifAdminStatusOid)) {
                    adminStatusMap.put(snmpIfIndex, vb.getVariable().toInt());
                } else if (currentOid.startsWith(ifOperStatusOid)) {
                    operStatusMap.put(snmpIfIndex, vb.getVariable().toInt());
                } else if (currentOid.startsWith(ifRxBytesOid)) {
                    rxBytesMap.put(snmpIfIndex, vb.getVariable().toLong());
                } else if (currentOid.startsWith(ifRxUcastPacketsOid)) {
                    SnmpInterfaceList.addSummarizedValue(rxPacketsMap, snmpIfIndex, vb.getVariable().toLong());
                } else if (currentOid.startsWith(ifRxNUcastPacketsOid)) {
                    SnmpInterfaceList.addSummarizedValue(rxPacketsMap, snmpIfIndex, vb.getVariable().toLong());
                } else if (currentOid.startsWith(ifRxDiscardsOid)) {
                    SnmpInterfaceList.addSummarizedValue(rxDDMap, snmpIfIndex, vb.getVariable().toLong());
                } else if (currentOid.startsWith(ifRxErrorsOid)) {
                    SnmpInterfaceList.addSummarizedValue(rxDDMap, snmpIfIndex, vb.getVariable().toLong());
                } else if (currentOid.startsWith(ifTxBytesOid)) {
                    txBytesMap.put(snmpIfIndex, vb.getVariable().toLong());
                } else if (currentOid.startsWith(ifTxUcastPacketsOid)) {
                    SnmpInterfaceList.addSummarizedValue(txPacketsMap, snmpIfIndex, vb.getVariable().toLong());
                } else if (currentOid.startsWith(ifTxNUcastPacketsOid)) {
                    SnmpInterfaceList.addSummarizedValue(txPacketsMap, snmpIfIndex, vb.getVariable().toLong());
                } else if (currentOid.startsWith(ifTxDiscardsOid)) {
                    SnmpInterfaceList.addSummarizedValue(txDDMap, snmpIfIndex, vb.getVariable().toLong());
                } else if (currentOid.startsWith(ifTxErrorsOid)) {
                    SnmpInterfaceList.addSummarizedValue(txDDMap, snmpIfIndex, vb.getVariable().toLong());
                }
                if (!indexList.contains(snmpIfIndex)) {
                    indexList.add(snmpIfIndex);
                }
            } else {
                throw new SnmpException("Unknown OID in SNMP response for interface table " + vb.getOid());
            }
        }
        indexList.stream().forEach((sii) -> {
            list.add(new SnmpInterface(indexMap.get(sii), nameMap.get(sii), typeMap.get(sii), adminStatusMap.get(sii), operStatusMap.get(sii), rxBytesMap.get(sii), txBytesMap.get(sii), rxPacketsMap.get(sii), txPacketsMap.get(sii), rxDDMap.get(sii), txDDMap.get(sii)));
        });
        return list;
    }

    private static void addSummarizedValue(Map<Integer, Long> targetMap, int snmpIfIndex, long value) {
        if (targetMap.containsKey(snmpIfIndex)) {
            long cv = targetMap.get(snmpIfIndex);
            targetMap.remove(snmpIfIndex);
            targetMap.put(snmpIfIndex, cv + value);
        } else {
            targetMap.put(snmpIfIndex, value);
        }
    }

    public E getInterfaceByIfIndex(int index) {
        for (E iface : this) {
            if (iface.getIndex() == index) {
                return iface;
            }
        }
        return null;
    }

}
