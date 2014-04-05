/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ctrdn.stuba.dbs.netmonitor.client.view;

import com.google.common.base.Preconditions;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import net.ctrdn.stuba.dbs.netmonitor.client.Client;
import net.ctrdn.stuba.dbs.netmonitor.client.annotation.NetmonitorView;
import net.ctrdn.stuba.dbs.netmonitor.client.exception.InitializationException;

/**
 *
 * @author castor
 */
public class MainFrame extends javax.swing.JFrame {

    private class AutoRefresh implements Runnable {

        private int refreshInterval = -1;
        private Date lastRefresh = new Date();

        @Override
        public void run() {
            try {
                while (true) {
                    if (this.getRefreshInterval() > -1 && new Date().getTime() - this.lastRefresh.getTime() > this.getRefreshInterval() * 1000 && MainFrame.this.activeView != null) {
                        MainFrame.this.activeView.refresh();
                        this.lastRefresh = new Date();
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ex) {

            }
        }

        public int getRefreshInterval() {
            return refreshInterval;
        }

        public void setRefreshInterval(int refreshInterval) {
            this.refreshInterval = refreshInterval;
        }
    }

    private final Client client;
    private Map<String, Integer> autorefreshMap = new HashMap<>();
    private final List<View> viewList = new ArrayList<>();
    private View activeView = null;
    private final AutoRefresh ar = new AutoRefresh();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void enableOSXFullscreen(Window window) throws InitializationException {
        Preconditions.checkNotNull(window);
        try {
            Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
            Class params[] = new Class[]{Window.class, Boolean.TYPE};
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);
        } catch (ClassNotFoundException e1) {
        } catch (Exception e) {
            InitializationException finalEx = new InitializationException("Unable to enable native Mac OS X fullscreen");
            finalEx.addSuppressed(e);
            throw finalEx;
        }
    }

    public MainFrame(Client client) throws InitializationException {
        MainFrame.enableOSXFullscreen(this);
        initComponents();
        new Thread(this.ar).start();
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenDimension.width / 2) - (this.getWidth() / 2), (screenDimension.height / 2) - (this.getHeight() / 2));

        this.autorefreshMap.put("Disabled", -1);
        this.autorefreshMap.put("Every second", 1);
        this.autorefreshMap.put("Every 5 seconds", 5);
        this.autorefreshMap.put("Every 15 seconds", 15);
        this.autorefreshMap.put("Every 30 seonds", 30);
        this.autorefreshMap.put("Every minute", 60);
        this.autorefreshMap.put("Every 5 minutes", 300);

        List<String> autorefreshList = new ArrayList<>();
        for (Map.Entry<String, Integer> mapEntry : this.autorefreshMap.entrySet()) {
            autorefreshList.add(mapEntry.getKey());
        }
        Collections.sort(autorefreshList);

        DefaultComboBoxModel arComboModel = new DefaultComboBoxModel();
        for (String name : autorefreshList) {
            arComboModel.addElement(name);
            if (this.ar.refreshInterval == this.autorefreshMap.get(name).intValue()) {
                arComboModel.setSelectedItem(name);
            }
        }
        this.comboAutorefresh.setModel(arComboModel);

        this.client = client;
        this.jList1.setModel(new DefaultListModel());
    }

    public void addView(View view) {
        this.viewList.add(view);
        DefaultListModel dlm = (DefaultListModel) this.jList1.getModel();
        dlm.add(dlm.size(), view.getClass().getDeclaredAnnotation(NetmonitorView.class).displayName());
        this.jList1.setModel(dlm);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        comboAutorefresh = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(150, 23));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Welcome!" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setMinimumSize(new java.awt.Dimension(150, 85));
        jList1.setPreferredSize(new java.awt.Dimension(150, 85));
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 502, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 473, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel1);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel1.setText("Autorefresh:");
        jToolBar1.add(jLabel1);

        comboAutorefresh.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboAutorefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAutorefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(comboAutorefresh);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        int index = this.jList1.getSelectedIndex();
        this.activeView = this.viewList.get(index);
        this.activeView.refresh();
        this.jSplitPane1.setRightComponent(this.activeView.getPanel());
    }//GEN-LAST:event_jList1ValueChanged

    private void comboAutorefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAutorefreshActionPerformed
        this.ar.setRefreshInterval(this.autorefreshMap.get((String) this.comboAutorefresh.getSelectedItem()));
    }//GEN-LAST:event_comboAutorefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboAutorefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
