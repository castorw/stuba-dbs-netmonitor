package net.ctrdn.stuba.dbs.netmonitor.client.view.device;

import java.awt.Color;
import net.ctrdn.stuba.dbs.netmonitor.client.view.NonEditableColorableDefaultTableModel;

public class ProbeTypeSelectorTableModel extends NonEditableColorableDefaultTableModel {

    private boolean editMode = false;

    public ProbeTypeSelectorTableModel() {
        super();
    }

    public ProbeTypeSelectorTableModel(Object[] columns, int rowCount) {
        super(columns, rowCount);
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        for (int i = 0; i < this.getRowCount(); i++) {
            if (this.editMode) {
                this.setRowColor(i, Color.PINK);
            }
        }
    }

    @Override
    public boolean isCellEditable(int a, int b) {
        return (b == 2 && editMode);
    }
}
