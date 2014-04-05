package net.ctrdn.stuba.dbs.netmonitor.client.view;

import javax.swing.JPanel;
import net.ctrdn.stuba.dbs.netmonitor.client.Client;

public interface View {

    public JPanel getPanel();

    public Client getClient();

    public void refresh();
}
