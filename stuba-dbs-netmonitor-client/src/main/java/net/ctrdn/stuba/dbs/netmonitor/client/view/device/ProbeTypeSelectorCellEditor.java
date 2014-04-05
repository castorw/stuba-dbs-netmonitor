package net.ctrdn.stuba.dbs.netmonitor.client.view.device;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

public class ProbeTypeSelectorCellEditor extends DefaultCellEditor {

    private final DefaultComboBoxModel model;

    public ProbeTypeSelectorCellEditor() {
        super(new JComboBox());
        this.model = (DefaultComboBoxModel) ((JComboBox) getComponent()).getModel();
        this.model.addElement("Yes");
        this.model.addElement("No");
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
}
