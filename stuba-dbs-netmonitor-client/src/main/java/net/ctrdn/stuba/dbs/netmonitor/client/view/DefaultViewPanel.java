package net.ctrdn.stuba.dbs.netmonitor.client.view;

import javax.swing.JPanel;

abstract public class DefaultViewPanel extends JPanel {

    protected final View view;

    public DefaultViewPanel(View view) {
        this.view = view;
    }

    abstract public void refresh();
}
