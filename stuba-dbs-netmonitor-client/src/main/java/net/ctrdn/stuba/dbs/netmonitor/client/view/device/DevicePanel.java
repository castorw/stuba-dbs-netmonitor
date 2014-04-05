/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ctrdn.stuba.dbs.netmonitor.client.view.device;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.DefaultViewPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.NonEditableColorableDefaultTableModel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;

/**
 *
 * @author castor
 */
public class DevicePanel extends DefaultViewPanel {
    
    public DevicePanel(View view) {
        super(view);
        initComponents();
        this.tableDeviceList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable) e.getSource();
                    int deviceId = (Integer) target.getValueAt(target.getSelectedRow(), 0);
                    DetailDialog dialog = new DetailDialog(DevicePanel.this.getView(), DevicePanel.this.getView().getDevice(deviceId), DevicePanel.this.getView().getClient().getApplicationMainFrame(), true);
                    dialog.setVisible(true);
                }
            }
        });
    }
    
    public DeviceView getView() {
        return (DeviceView) this.view;
    }
    
    @Override
    public void refresh() {
        DefaultTableModel tableModel = new NonEditableColorableDefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"ID", "Name", "Description", "IPv4 Address", "Last data", "Modified", "Created"});
        for (NmDevice deviceRecord : this.getView().getDeviceList()) {
            Date lastData = null;
            if (deviceRecord.getNmProbes().size() > 0) {
                for (NmProbe probeRecord : (Set<NmProbe>) deviceRecord.getNmProbes()) {
                    if (probeRecord.getLastUpdateDate() != null) {
                        if (lastData == null || lastData.getTime() < probeRecord.getLastUpdateDate().getTime()) {
                            lastData = probeRecord.getLastUpdateDate();
                        }
                    }
                }
            }
            tableModel.addRow(new Object[]{deviceRecord.getId(), deviceRecord.getDeviceName(), deviceRecord.getDeviceDescription(), deviceRecord.getIpv4Address(), (lastData == null) ? "-" : lastData, deviceRecord.getModifiedDate(), deviceRecord.getCreateDate()});
        }
        this.tableDeviceList.setModel(tableModel);
        this.tableDeviceList.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tableDeviceList.getColumnModel().getColumn(1).setPreferredWidth(225);
        this.tableDeviceList.getColumnModel().getColumn(1).setMaxWidth(250);
        this.tableDeviceList.getColumnModel().getColumn(3).setPreferredWidth(150);
        this.tableDeviceList.getColumnModel().getColumn(3).setMaxWidth(150);
        this.tableDeviceList.getColumnModel().getColumn(4).setPreferredWidth(200);
        this.tableDeviceList.getColumnModel().getColumn(4).setMaxWidth(200);
        this.tableDeviceList.getColumnModel().getColumn(5).setPreferredWidth(200);
        this.tableDeviceList.getColumnModel().getColumn(5).setMaxWidth(200);
        this.tableDeviceList.getColumnModel().getColumn(6).setPreferredWidth(200);
        this.tableDeviceList.getColumnModel().getColumn(6).setMaxWidth(200);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar2 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableDeviceList = new javax.swing.JTable();

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        jButton1.setText("Refresh");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton1);

        jButton2.setText("Add device");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton2);

        tableDeviceList.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tableDeviceList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        DetailDialog dialog = new DetailDialog(this.getView(), null, this.getView().getClient().getApplicationMainFrame(), true);
        dialog.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.getView().refresh();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JTable tableDeviceList;
    // End of variables declaration//GEN-END:variables
}
