package net.ctrdn.stuba.dbs.netmonitor.client.view.snmpinterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.DefaultViewPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.NonEditableColorableDefaultTableModel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmInterface;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmInterfaceAggregatedStats;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmInterfaceStats;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;

public class SnmpInterfacePanel extends DefaultViewPanel {

    private NmDevice selectedDeviceRecord = null;
    private NmProbe selectedProbeRecord = null;
    private NmInterface selectedInterfaceRecord = null;

    public SnmpInterfacePanel(View view) {
        super(view);
        initComponents();
    }

    private SnmpInterfaceView getView() {
        return (SnmpInterfaceView) this.view;
    }

    @Override
    public void refresh() {
        DefaultComboBoxModel deviceComboModel = new DefaultComboBoxModel();
        deviceComboModel.addElement("Select device...");
        for (NmDevice deviceRecord : this.getView().getDeviceList()) {
            String newObj = deviceRecord.getId() + ", " + deviceRecord.getDeviceName();
            deviceComboModel.addElement(newObj);
            if (this.selectedDeviceRecord != null && this.selectedDeviceRecord.getId() == deviceRecord.getId()) {
                deviceComboModel.setSelectedItem(newObj);
            }
        }
        this.comboDeviceList.setModel(deviceComboModel);

        DefaultComboBoxModel interfaceComboModel = new DefaultComboBoxModel();
        interfaceComboModel.addElement("Select interface...");
        NonEditableColorableDefaultTableModel statsTableModel = new NonEditableColorableDefaultTableModel();
        statsTableModel.setColumnIdentifiers(new Object[]{"Sample Date", "Admin Status", "Operational Status", "Rx Bytes", "Tx Bytes", "Rx Packets", "Tx Packets", "Rx Discards", "Tx Discards"});

        this.labelInterfaceCount.setText("-");
        this.labelRxBytes.setText("-");
        this.labelTxBytes.setText("-");
        this.labelRxPackets.setText("-");
        this.labelTxPackets.setText("-");
        this.labelAggregationDate.setText("-");

        if (this.selectedDeviceRecord != null) {
            NmInterfaceAggregatedStats aggregatedStatsRecord = this.getView().getAggregatedStats(this.selectedProbeRecord);
            if (aggregatedStatsRecord != null) {
                this.labelInterfaceCount.setText(Integer.toString(aggregatedStatsRecord.getInterfaceCount()));
                this.labelRxBytes.setText(SnmpInterfaceView.getReadableByteSize(aggregatedStatsRecord.getRxBytes()));
                this.labelTxBytes.setText(SnmpInterfaceView.getReadableByteSize(aggregatedStatsRecord.getTxBytes()));
                this.labelRxPackets.setText(Long.toString(aggregatedStatsRecord.getRxPackets()));
                this.labelTxPackets.setText(Long.toString(aggregatedStatsRecord.getTxPackets()));
                this.labelAggregationDate.setText(aggregatedStatsRecord.getCreateDate().toString());
            }
            Set<NmInterface> interfaceSet = this.selectedProbeRecord.getNmInterfaces();
            List<NmInterface> interfaceList = new ArrayList<>(interfaceSet);
            Collections.sort(interfaceList, new Comparator<NmInterface>() {

                @Override
                public int compare(NmInterface o1, NmInterface o2) {
                    return o1.getId() < o2.getId() ? -1 : o1.getId() == o2.getId() ? 0 : 1;
                }
            });
            for (NmInterface interfaceRecord : interfaceList) {
                String newObj = interfaceRecord.getId() + ", " + interfaceRecord.getInterfaceName();
                interfaceComboModel.addElement(newObj);
                System.out.println(((this.selectedInterfaceRecord != null) ? this.selectedInterfaceRecord.getId() : "NULL") + " - " + interfaceRecord.getId());
                if (this.selectedInterfaceRecord != null && this.selectedInterfaceRecord.getId().equals(interfaceRecord.getId())) {
                    interfaceComboModel.setSelectedItem(newObj);
                    System.out.println("-SELECTED-");
                }
            }
            if (this.selectedInterfaceRecord != null) {
                Set<NmInterfaceStats> statsSet = this.selectedInterfaceRecord.getNmInterfaceStatses();
                List<NmInterfaceStats> statsList = new ArrayList<>(statsSet);
                Collections.sort(statsList, new Comparator<NmInterfaceStats>() {

                    @Override
                    public int compare(NmInterfaceStats o1, NmInterfaceStats o2) {
                        return o1.getCreateDate().getTime() < o2.getCreateDate().getTime() ? -1 : o1.getCreateDate().getTime() == o2.getCreateDate().getTime() ? 0 : 1;
                    }
                });
                for (NmInterfaceStats ifStats : statsList) {
                    String adminStatus = (ifStats.getInterfaceAdminStatus() == 1) ? "Yes" : "No";
                    String operStatus = (ifStats.getInterfaceOperationalStatus() == 1) ? "Yes" : "No";
                    statsTableModel.addRow(new Object[]{ifStats.getCreateDate().toString(), adminStatus, operStatus, SnmpInterfaceView.getReadableByteSize(ifStats.getInterfaceRxBytes()), SnmpInterfaceView.getReadableByteSize(ifStats.getInterfaceTxBytes()), ifStats.getInterfaceRxPackets(), ifStats.getInterfaceTxPackets(), ifStats.getInterfaceRxDiscardsDrops(), ifStats.getInterfaceTxDiscardsDrops()});
                }
            }
        }

        this.comboInterfaceList.setModel(interfaceComboModel);
        this.tableStats.setModel(statsTableModel);
        this.tableStats.getColumnModel().getColumn(0).setPreferredWidth(200);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        comboDeviceList = new javax.swing.JComboBox();
        buttonViewDevice = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labelInterfaceCount = new javax.swing.JLabel();
        labelRxBytes = new javax.swing.JLabel();
        labelTxBytes = new javax.swing.JLabel();
        labelRxPackets = new javax.swing.JLabel();
        labelTxPackets = new javax.swing.JLabel();
        labelAggregationDate = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableStats = new javax.swing.JTable();
        jToolBar2 = new javax.swing.JToolBar();
        comboInterfaceList = new javax.swing.JComboBox();
        buttonViewInterface = new javax.swing.JButton();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        comboDeviceList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jToolBar1.add(comboDeviceList);

        buttonViewDevice.setText("View...");
        buttonViewDevice.setFocusable(false);
        buttonViewDevice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonViewDevice.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonViewDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonViewDeviceActionPerformed(evt);
            }
        });
        jToolBar1.add(buttonViewDevice);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setText("Latest aggregated stats");

        jLabel2.setText("Interface count:");

        jLabel3.setText("Received bytes:");

        jLabel4.setText("Transmitted bytes:");

        jLabel5.setText("Received packets:");

        jLabel6.setText("Transmitted packets:");

        jLabel7.setText("Aggregated at:");

        labelInterfaceCount.setText("-");

        labelRxBytes.setText("-");

        labelTxBytes.setText("-");

        labelRxPackets.setText("-");

        labelTxPackets.setText("-");

        labelAggregationDate.setText("-");

        tableStats.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tableStats);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        comboInterfaceList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jToolBar2.add(comboInterfaceList);

        buttonViewInterface.setText("View...");
        buttonViewInterface.setFocusable(false);
        buttonViewInterface.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonViewInterface.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonViewInterface.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonViewInterfaceActionPerformed(evt);
            }
        });
        jToolBar2.add(buttonViewInterface);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelRxBytes, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelInterfaceCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelTxBytes, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelRxPackets, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelTxPackets, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelAggregationDate, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(labelInterfaceCount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(labelRxBytes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(labelTxBytes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(labelRxPackets))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(labelTxPackets))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(labelAggregationDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonViewDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonViewDeviceActionPerformed
        if (this.comboDeviceList.getSelectedIndex() == 0) {
            this.selectedDeviceRecord = null;
            this.selectedProbeRecord = null;
            this.selectedInterfaceRecord = null;
        } else {
            String valueSplit[] = ((String) this.comboDeviceList.getSelectedItem()).split(",");
            int deviceId = Integer.parseInt(valueSplit[0]);
            SnmpInterfaceView.SnmpDeviceContainer container = this.getView().getDevice(deviceId);
            this.selectedDeviceRecord = container.getDevice();
            this.selectedProbeRecord = container.getProbe();
        }
        this.refresh();
    }//GEN-LAST:event_buttonViewDeviceActionPerformed

    private void buttonViewInterfaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonViewInterfaceActionPerformed
        if (this.comboInterfaceList.getSelectedIndex() == 0) {
            this.selectedInterfaceRecord = null;
        } else {
            String valueSplit[] = ((String) this.comboInterfaceList.getSelectedItem()).split(",");
            int interfaceId = Integer.parseInt(valueSplit[0]);
            this.selectedInterfaceRecord = this.getView().getInterface(interfaceId);
        }
        this.refresh();
    }//GEN-LAST:event_buttonViewInterfaceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonViewDevice;
    private javax.swing.JButton buttonViewInterface;
    private javax.swing.JComboBox comboDeviceList;
    private javax.swing.JComboBox comboInterfaceList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel labelAggregationDate;
    private javax.swing.JLabel labelInterfaceCount;
    private javax.swing.JLabel labelRxBytes;
    private javax.swing.JLabel labelRxPackets;
    private javax.swing.JLabel labelTxBytes;
    private javax.swing.JLabel labelTxPackets;
    private javax.swing.JTable tableStats;
    // End of variables declaration//GEN-END:variables
}
