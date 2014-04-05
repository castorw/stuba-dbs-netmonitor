package net.ctrdn.stuba.dbs.netmonitor.client.view.probetype;

import net.ctrdn.stuba.dbs.netmonitor.client.view.DefaultViewPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.NonEditableColorableDefaultTableModel;
import net.ctrdn.stuba.dbs.netmonitor.client.view.View;
import net.ctrdn.stuba.dbs.netmonitor.hbm.NmProbeType;

public class ProbeTypePanel extends DefaultViewPanel {
    
    public ProbeTypePanel(View view) {
        super(view);
        initComponents();
    }
    
    private ProbeTypeView getView() {
        return (ProbeTypeView) this.view;
    }
    
    @Override
    public void refresh() {
        NonEditableColorableDefaultTableModel tm = new NonEditableColorableDefaultTableModel();
        tm.setColumnIdentifiers(new Object[]{"ID", "Name", "Description", "Classpath", "Instances"});
        for (NmProbeType pt : this.getView().getProbeTypes()) {
            tm.addRow(new Object[]{pt.getId(), pt.getProbeName(), pt.getProbeDescription(), pt.getProbeClasspath(), pt.getNmProbes().size()});
        }
        this.tableProbeTypes.setModel(tm);
        this.tableProbeTypes.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tableProbeTypes.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.tableProbeTypes.getColumnModel().getColumn(1).setMaxWidth(250);
        this.tableProbeTypes.getColumnModel().getColumn(1).setPreferredWidth(200);
        this.tableProbeTypes.getColumnModel().getColumn(4).setMaxWidth(100);
        this.tableProbeTypes.getColumnModel().getColumn(4).setPreferredWidth(80);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableProbeTypes = new javax.swing.JTable();

        tableProbeTypes.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableProbeTypes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableProbeTypes;
    // End of variables declaration//GEN-END:variables
}
