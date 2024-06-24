package net.sourceforge.squirrel_sql.client.session.mainpanel.rotatedtable;

import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.lazyresulttab.LazyTabControllerCtrl;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultTableType;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DataSetViewerFindHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Formatter;

public class RotatedTableCtrl implements LazyTabControllerCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RotatedTableCtrl.class);

   private ISession _session;

   private RotatedTablePanel _rotatedTablePanel = new RotatedTablePanel();

   private DataSetViewerFindHandler _dataSetViewerFindHandler;


   public RotatedTableCtrl(ISession session)
   {
      _session = session;
   }

   public static boolean isRotatedTablePanel(Component comp)
   {
      return comp instanceof RotatedTablePanel;
   }

   public String getTitle()
   {
      return s_stringMgr.getString("rotatedTable.tab.name");
   }

   public RotatedTablePanel getPanel()
   {
      return _rotatedTablePanel;
   }

   public void init(ResultSetDataSet rsds)
   {

      try
      {

         int rowCount = rsds.getAllDataForReadOnly().size();

         ColumnDisplayDefinition rotatedColDefs[] = new ColumnDisplayDefinition[rowCount + 1];

         rotatedColDefs[0] = new ColumnDisplayDefinition(20, s_stringMgr.getString("rotatedTable.ColName"));
         for (int i = 0; i < rowCount; i++)
         {
            rotatedColDefs[i + 1] = new ColumnDisplayDefinition(20, s_stringMgr.getString("rotatedTable.rotatedCol", i+1));
         }

         ArrayList<Object[]> rotatedRows = new ArrayList<Object[]>();


         for (int i = 0; i < rsds.getDataSetDefinition().getColumnDefinitions().length; i++)
         {
            ColumnDisplayDefinition columnDisplayDefinition = rsds.getDataSetDefinition().getColumnDefinitions()[i];

            Object[] rotatedRow = new Object[rowCount + 1];
            rotatedRow[0] = columnDisplayDefinition.getColumnName();

            for (int j = 0; j < rowCount; j++)
            {
               Object[] row = rsds.getAllDataForReadOnly().get(j);

               if (row[i] instanceof Byte[] || row[i] instanceof byte[])
               {
                  Byte[] cellObjBytes;
                  if (row[i] instanceof Byte[])
                  {
                     cellObjBytes = (Byte[]) row[i];
                  }
                  else
                  {
                     byte[] cellObjBytesPrimitives = (byte[]) row[i];
                     cellObjBytes = new Byte[cellObjBytesPrimitives.length];
                     int k = 0;
                     for (byte b : cellObjBytesPrimitives)
                     {
                        cellObjBytes[k++] = b;
                     }
                  }

                  Formatter formatter = new Formatter();
                  String format = "%02x";
                  for (byte b : cellObjBytes)
                  {
                     formatter.format(format, b);
                     format = "  %02x";
                  }
                  String cellObjStr = formatter.toString();
                  formatter.close();

                  rotatedRow[j + 1] = cellObjStr;
               }
               else
               {
                  rotatedRow[j + 1] = row[i];
               }
            }

            rotatedRows.add(rotatedRow);

         }

         SimpleDataSet simpleDataSet = new SimpleDataSet(rotatedRows, rotatedColDefs);

         DataSetViewerTablePanel table = new DataSetViewerTablePanel();
         table.init(null, new DataModelImplementationDetails(_session), _session);
         table.show(simpleDataSet);

         _dataSetViewerFindHandler = new DataSetViewerFindHandler(table, _session, ResultTableType.ROTATED_SQL_RESULT_TABLE);

         _rotatedTablePanel.add(_dataSetViewerFindHandler.getComponent());

      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }

   public DataSetViewerFindHandler getDataSetViewerFindHandler()
   {
      return _dataSetViewerFindHandler;
   }
}
