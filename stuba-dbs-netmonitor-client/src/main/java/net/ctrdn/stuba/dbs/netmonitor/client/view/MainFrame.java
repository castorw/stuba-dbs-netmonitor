/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ctrdn.stuba.dbs.netmonitor.client.view;

import com.apple.eawt.Application;
import com.google.common.base.Preconditions;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import net.ctrdn.stuba.dbs.netmonitor.client.Client;
import net.ctrdn.stuba.dbs.netmonitor.client.annotation.NetmonitorView;
import net.ctrdn.stuba.dbs.netmonitor.client.exception.InitializationException;

/**
 *
 * @author castor
 */
public class MainFrame extends javax.swing.JFrame {

    private final Client client;

    private final List<View> viewList = new ArrayList<>();

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
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenDimension.width / 2) - (this.getWidth() / 2), (screenDimension.height / 2) - (this.getHeight() / 2));
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
            .addGap(0, 504, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        int index = this.jList1.getSelectedIndex();
        View view = this.viewList.get(index);
        view.refresh();
        this.jSplitPane1.setRightComponent(view.getPanel());
    }//GEN-LAST:event_jList1ValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
}
