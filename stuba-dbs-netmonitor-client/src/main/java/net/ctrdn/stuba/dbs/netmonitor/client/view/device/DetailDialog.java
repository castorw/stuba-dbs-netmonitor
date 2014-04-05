package net.ctrdn.stuba.dbs.netmonitor.client.view.device;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import net.ctrdn.stuba.dbs.netmonitor.client.view.ColorableTableCellRenderer;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmDevice;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbe;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbeType;

public class DetailDialog extends javax.swing.JDialog {
    
    private final DeviceView view;
    private NmDevice deviceRecord;
    private boolean editMode = false;
    private ProbeTypeSelectorTableModel probeSelectorTableModel;
    
    public DetailDialog(DeviceView view, NmDevice deviceRecord, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.deviceRecord = deviceRecord;
        this.view = view;
        initComponents();
        this.loadProbeTypes();
        this.setLocation(parent.getLocation().x + (parent.getWidth() / 2) - (this.getWidth() / 2), parent.getLocation().y + (parent.getHeight() / 2) - (this.getHeight() / 2));
        this.mapKeyEvents();
        
        if (this.deviceRecord == null) {
            this.loadAddMode();
        } else {
            this.loadViewMode();
        }
    }
    
    private void loadProbeTypes() {
        this.probeSelectorTableModel = new ProbeTypeSelectorTableModel();
        this.probeSelectorTableModel.setColumnIdentifiers(new Object[]{"ID", "Probe Type", "Enabled"});
        for (NmProbeType pt : this.view.getProbeTypes()) {
            boolean selected = false;
            if (this.deviceRecord != null && this.deviceRecord.getId() != null) {
                for (NmProbe probe : (Set<NmProbe>) this.deviceRecord.getNmProbes()) {
                    if (probe.getNmProbeType().getId() == pt.getId()) {
                        selected = true;
                        break;
                    }
                }
            }
            this.probeSelectorTableModel.addRow(new Object[]{pt.getId(), pt.getProbeName(), (selected) ? "Yes" : "No"});
        }
        this.tableProbes.setModel(this.probeSelectorTableModel);
        this.tableProbes.getColumnModel().getColumn(2).setCellEditor(new ProbeTypeSelectorCellEditor());
        this.tableProbes.setDefaultRenderer(Object.class, new ColorableTableCellRenderer());
    }
    
    private void loadData() {
        this.labelId.setText(Integer.toString(this.deviceRecord.getId()));
        this.tfDeviceName.setText(this.deviceRecord.getDeviceName());
        this.tfDeviceDescription.setText(this.deviceRecord.getDeviceDescription());
        this.tfDeviceIpV4Address.setText(this.deviceRecord.getIpv4Address());
        this.labelLastModified.setText((this.deviceRecord.getModifiedDate() == null) ? "-" : this.deviceRecord.getModifiedDate().toString());
        this.labelCreated.setText(this.deviceRecord.getCreateDate().toString());
        this.loadProbeTypes();
    }
    
    private void saveData() {
        if (this.deviceRecord == null) {
            this.deviceRecord = new NmDevice();
            this.deviceRecord.setCreateDate(new Date());
            this.deviceRecord.setNmProbes(new HashSet());
        }
        this.deviceRecord.setDeviceName(tfDeviceName.getText());
        this.deviceRecord.setDeviceDescription(tfDeviceDescription.getText());
        this.deviceRecord.setIpv4Address(tfDeviceIpV4Address.getText());
        for (NmProbeType pt : this.view.getProbeTypes()) {
            for (int i = 0; i < this.tableProbes.getRowCount(); i++) {
                Integer ptId = (Integer) this.tableProbes.getValueAt(i, 0);
                String val = (String) this.tableProbes.getValueAt(i, 2);
                if (pt.getId() == ptId) {
                    NmProbe foundProbe = null;
                    for (NmProbe probe : (Set<NmProbe>) this.deviceRecord.getNmProbes()) {
                        if (probe.getNmProbeType().getId() == pt.getId()) {
                            foundProbe = probe;
                            break;
                        }
                    }
                    if (val.equals("Yes")) {
                        if (foundProbe == null) {
                            NmProbe probeRecord = new NmProbe();
                            probeRecord.setNmDevice(this.deviceRecord);
                            probeRecord.setNmProbeType(pt);
                            probeRecord.setCreateDate(new Date());
                            this.deviceRecord.getNmProbes().add(probeRecord);
                        }
                    } else {
                        if (foundProbe != null) {
                            this.deviceRecord.getNmProbes().remove(foundProbe);
                        }
                    }
                    break;
                }
            }
        }
        if (this.deviceRecord.getId() == null) {
            this.deviceRecord = this.view.addDevice(deviceRecord);
        } else {
            this.deviceRecord = this.view.updateDevice(this.deviceRecord);
        }
        this.loadViewMode();
        this.view.refresh();
    }
    
    private void deleteRecord() {
        if (this.deviceRecord != null && this.deviceRecord.getId() != null) {
            this.view.deleteDevice(this.deviceRecord);
            this.view.refresh();
            this.dispose();
        }
    }
    
    private void loadViewMode() {
        this.editMode = false;
        this.buttonReload.setEnabled(true);
        this.buttonEdit.setEnabled(true);
        this.buttonDelete.setEnabled(false);
        this.buttonDelete.setVisible(false);
        this.buttonSave.setEnabled(false);
        this.buttonSave.setVisible(false);
        this.buttonCancel.setEnabled(false);
        this.buttonCancel.setVisible(false);
        this.tfDeviceName.setEditable(false);
        this.tfDeviceDescription.setEditable(false);
        this.tfDeviceIpV4Address.setEditable(false);
        this.loadData();
        this.probeSelectorTableModel.setEditMode(false);
    }
    
    private void loadEditMode() {
        this.editMode = true;
        this.buttonReload.setEnabled(false);
        this.buttonEdit.setEnabled(false);
        this.buttonDelete.setEnabled(true);
        this.buttonDelete.setVisible(true);
        this.buttonSave.setEnabled(true);
        this.buttonSave.setVisible(true);
        this.buttonCancel.setEnabled(true);
        this.buttonCancel.setVisible(true);
        this.tfDeviceName.setEditable(true);
        this.tfDeviceDescription.setEditable(true);
        this.tfDeviceIpV4Address.setEditable(true);
        this.loadData();
        this.probeSelectorTableModel.setEditMode(true);
    }
    
    private void loadAddMode() {
        this.editMode = true;
        this.buttonReload.setEnabled(false);
        this.buttonEdit.setEnabled(false);
        this.buttonDelete.setEnabled(false);
        this.buttonDelete.setVisible(false);
        this.buttonSave.setEnabled(true);
        this.buttonSave.setVisible(true);
        this.buttonCancel.setEnabled(false);
        this.buttonCancel.setVisible(false);
        this.tfDeviceName.setEditable(true);
        this.tfDeviceDescription.setEditable(true);
        this.tfDeviceIpV4Address.setEditable(true);
        this.probeSelectorTableModel.setEditMode(true);
    }
    
    private void mapKeyEvents() {
        KeyStroke ksEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        KeyStroke ksEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        String dispatchWindowClosingActionMapKey = this.getClass().getName() + ":WINDOW_CLOSING";
        String dispatchSaveActionMapKey = this.getClass().getName() + ":SAVE_DATA";
        Action dispatchClosing = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                DetailDialog.this.dispatchEvent(new WindowEvent(DetailDialog.this, WindowEvent.WINDOW_CLOSING));
            }
        };
        Action dispatchSave = new AbstractAction() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (DetailDialog.this.editMode) {
                    DetailDialog.this.saveData();
                }
            }
        };
        
        JRootPane root = this.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ksEscape, dispatchWindowClosingActionMapKey);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ksEnter, dispatchSaveActionMapKey);
        root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
        root.getActionMap().put(dispatchSaveActionMapKey, dispatchSave);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        buttonReload = new javax.swing.JButton();
        buttonEdit = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tfDeviceName = new javax.swing.JTextField();
        tfDeviceDescription = new javax.swing.JTextField();
        tfDeviceIpV4Address = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        labelLastModified = new javax.swing.JLabel();
        labelCreated = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        labelId = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableProbes = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Devcice detail");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        buttonReload.setText("Refresh");
        buttonReload.setFocusable(false);
        buttonReload.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonReload.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReloadActionPerformed(evt);
            }
        });
        jToolBar1.add(buttonReload);

        buttonEdit.setText("Edit");
        buttonEdit.setFocusable(false);
        buttonEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });
        jToolBar1.add(buttonEdit);

        buttonDelete.setText("Delete");
        buttonDelete.setFocusable(false);
        buttonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(buttonDelete);

        buttonSave.setText("Save");
        buttonSave.setFocusable(false);
        buttonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(buttonSave);

        buttonCancel.setText("Cancel");
        buttonCancel.setFocusable(false);
        buttonCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(buttonCancel);

        jLabel1.setText("Device name:");

        jLabel2.setText("Device description:");

        jLabel3.setText("Device IPv4 address:");

        jLabel4.setText("Last modified:");

        jLabel5.setText("Created:");

        labelLastModified.setText("-");

        labelCreated.setText("-");

        jLabel6.setText("ID:");

        labelId.setText("-");

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel7.setText("Probes:");

        tableProbes.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableProbes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDeviceName))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDeviceIpV4Address))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfDeviceDescription))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelId))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelCreated))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelLastModified)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(labelId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfDeviceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfDeviceDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfDeviceIpV4Address, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(labelLastModified))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(labelCreated))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReloadActionPerformed
        this.deviceRecord = this.view.getDevice(this.deviceRecord.getId());
        this.loadViewMode();
    }//GEN-LAST:event_buttonReloadActionPerformed

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
        this.loadEditMode();
    }//GEN-LAST:event_buttonEditActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        this.loadViewMode();
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        this.saveData();
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        if (JOptionPane.showConfirmDialog(this.rootPane, "Are you sure you want to delete this device?", "Confirm device deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            this.deleteRecord();
        }
    }//GEN-LAST:event_buttonDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JButton buttonReload;
    private javax.swing.JButton buttonSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labelCreated;
    private javax.swing.JLabel labelId;
    private javax.swing.JLabel labelLastModified;
    private javax.swing.JTable tableProbes;
    private javax.swing.JTextField tfDeviceDescription;
    private javax.swing.JTextField tfDeviceIpV4Address;
    private javax.swing.JTextField tfDeviceName;
    // End of variables declaration//GEN-END:variables
}
