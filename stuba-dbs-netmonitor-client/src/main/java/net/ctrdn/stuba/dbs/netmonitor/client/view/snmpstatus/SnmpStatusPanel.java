package net.ctrdn.stuba.dbs.netmonitor.client.view.snmpstatus;

import net.ctrdn.stuba.dbs.netmonitor.client.view.DefaultViewPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.NonEditableColorableDefaultTableModel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;

public class SnmpStatusPanel extends DefaultViewPanel {

    public SnmpStatusPanel(View view) {
        super(view);
        initComponents();
    }

    public SnmpStatusView getView() {
        return (SnmpStatusView) this.view;
    }

    @Override
    public void refresh() {
        NonEditableColorableDefaultTableModel tm = new NonEditableColorableDefaultTableModel();
        tm.setColumnIdentifiers(new Object[]{"Device Name", "Probe Installed", "Probe Status", "Last Update", "Name", "Description", "Location", "Uptime"});
        for (SnmpStatusView.SnmpStatusRecord r : this.getView().getStatusRecords()) {
            String probeInstalled = (r.getProbeRecord() != null) ? "Yes" : "No";
            String probeStatus = (r.getProbeRecord() == null) ? "-" : (r.getProbeRecord().getProbeStatus() == 1) ? "OK" : "No data";
            String probeLastUpdate = (r.getProbeRecord() == null) ? "-" : (r.getProbeRecord().getLastUpdateDate() == null) ? "-" : r.getProbeRecord().getLastUpdateDate().toString();
            tm.addRow(new Object[]{r.getDeviceRecord().getDeviceName(), probeInstalled, probeStatus, probeLastUpdate, r.getSnmpSysName(), r.getSnmpSysDescr(), r.getSnmpSysLocation(), r.getSnmpSysUptime()});
        }
        this.tableSnmpStatus.setModel(tm);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableSnmpStatus = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        buttonRefresh = new javax.swing.JButton();

        tableSnmpStatus.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableSnmpStatus);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        buttonRefresh.setText("Refresh");
        buttonRefresh.setFocusable(false);
        buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(buttonRefresh);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshActionPerformed
        this.getView().refresh();
    }//GEN-LAST:event_buttonRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonRefresh;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTable tableSnmpStatus;
    // End of variables declaration//GEN-END:variables
}
