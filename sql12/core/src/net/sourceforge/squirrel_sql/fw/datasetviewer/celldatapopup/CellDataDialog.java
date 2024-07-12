package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.Component;

class CellDataDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataDialog.class);

   public CellDataDialog(Component comp, String columnName, ColumnDisplayDefinition colDef,
                         Object value, int row, int col,
                         boolean isModelEditable, JTable table)
   {

      super(SwingUtilities.windowForComponent(comp), s_stringMgr.getString("cellDataPopup.valueofColumn", columnName));
      CellDataColumnDataPanel cellDataPanel = new CellDataColumnDataPanel(value, colDef, isModelEditable);
      cellDataPanel.setCellDataUpdateInfo(new CellDataUpdateInfo(row, col, table, this));
      setContentPane(cellDataPanel);

      GUIUtils.enableCloseByEscape(this);
   }

}
