package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableState;
import net.sourceforge.squirrel_sql.fw.gui.table.SortableTable;
import net.sourceforge.squirrel_sql.fw.gui.table.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataScaleTable extends SortableTable
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataScaleTable.class);


   private List<Object[]> _allRows;
   private ColumnDisplayDefinition[] _columnDefinitions;

   private DataScaleTable _parent;
   private DataScaleTable _kid;
   private DataSetViewerTablePanel _kidSimpleTable;

   public DataScaleTable(DataScaleTableModel dataScaleTableModel, List<Object[]> allRows, ColumnDisplayDefinition[] columnDefinitions)
   {
      super(dataScaleTableModel);
      _allRows = allRows;
      _columnDefinitions = columnDefinitions;

      setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      new DataScaleTablePopupHandler(this);

      new DataScaleTableColumnWidthsPersister(getTableHeader());
   }


   @Override
   public TableCellRenderer getCellRenderer(int row, int column)
   {
      String headerValue = (String) getColumnModel().getColumn(column).getHeaderValue();
      if(DataScaleTableModel.COL_NAME_COLUMN.equals(headerValue))
      {
         TableCellRenderer cellRenderer = super.getCellRenderer(row, column);

         JLabel label = (JLabel) cellRenderer.getTableCellRendererComponent(this, headerValue, false, false, row, column);
         label.setToolTipText(s_stringMgr.getString("DataScaleTable.columnNameToolTip"));

         return cellRenderer;
      }
      else
      {
         DataScaleTableModel model = getDataScaleTableModel();
         return createScaleDataCellRenderer(model);
      }
   }

   @Override
   public TableCellEditor getCellEditor(int row, int column)
   {
      String headerValue = (String) getColumnModel().getColumn(column).getHeaderValue();
      if(DataScaleTableModel.COL_NAME_COLUMN.equals(headerValue))
      {
         return super.getCellEditor(row, column);
      }
      else
      {
         DataScaleTableModel model = getDataScaleTableModel();

         SortableTableModel sortableTableModel = (SortableTableModel) getModel();
         return new DataScaleTableCellEditor(model.getDataScaleAt(sortableTableModel.transformToModelRow(row)));
      }
   }

   public DataScaleTableModel getDataScaleTableModel()
   {
      TableModel ret = ((SortableTableModel) getModel()).getActualModel();

      while(ret instanceof SortableTableModel)
      {
         ret = ((SortableTableModel)ret).getActualModel();
      }

      return (DataScaleTableModel) ret;
   }


   private TableCellRenderer createScaleDataCellRenderer(final DataScaleTableModel dataScaleTableModel)
   {
      return new TableCellRenderer()
      {
         @Override
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
         {
            SortableTableModel sortableTableModel = (SortableTableModel) getModel();
            return dataScaleTableModel.getDataScaleAt(sortableTableModel.transformToModelRow(row)).getPanel();
         }
      };
   }

   public List<Object[]> getAllRows()
   {
      return _allRows;
   }

   public ColumnDisplayDefinition[] getColumnDisplayDefinitions()
   {
      return _columnDefinitions;
   }

   public void setParentScaleTable(DataScaleTable parent)
   {
      TableState st = new TableState(parent);
      st.apply(this);

      _parent = parent;

      _parent.setKidScaleTable(this);
   }

   public void setKidScaleTable(DataScaleTable kid)
   {
      _kid = kid;
      _kidSimpleTable = null;
   }

   public DataScaleTable getKidScaleTable()
   {
      return _kid;
   }

   public DataScaleTable getParentScaleTable()
   {
      return _parent;
   }

   public void setKidSimpleTable(DataSetViewerTablePanel simpleTable)
   {
      _kidSimpleTable = simpleTable;
      _kid = null;
   }

   public DataSetViewerTablePanel getKidSimpleTable()
   {
      return _kidSimpleTable;
   }

   public ArrayList<Double> getDoubleValuesForColumn(ColumnDisplayDefinition columnDisplayDefinition)
   {
      for (int i = 0; i < _columnDefinitions.length; i++)
      {
         if(_columnDefinitions[i] == columnDisplayDefinition)
         {
            ArrayList<Double> ret = new ArrayList<Double>();

            for (Object[] row : _allRows)
            {
               double value = 0;

               if (null != row[i])
               {
                  if (row[i] instanceof Date)
                  {
                     value = ((Date) row[i]).getTime();
                  }
                  else
                  {
                     value = ((Number) row[i]).doubleValue();
                  }
               }

               ret.add(value);
            }

            return ret;
         }

      }

      throw new IllegalArgumentException("Column not found " + columnDisplayDefinition.getColumnName());
   }
}
