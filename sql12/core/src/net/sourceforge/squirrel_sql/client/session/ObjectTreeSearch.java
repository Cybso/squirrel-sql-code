package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.schemainfo.FilterMatcher;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;
import java.util.ArrayList;


/**
 * Helps to locate Objects in the Object tree of a Session main window or an ObjectTreeInternalFrame
 */
public class ObjectTreeSearch
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ObjectTreeSearch.class);

   /**
    * View the Object at cursor in the Object Tree
    *
    * @param	evt		Event being executed.
    */
   public void viewObjectInObjectTree(String objectName, ISession session)
   {


      if(false == session.getActiveSessionWindow() instanceof SessionInternalFrame &&
         false == session.getActiveSessionWindow() instanceof ObjectTreeInternalFrame)
      {
         return;
      }


      IObjectTreeAPI objectTreeAPI = session.getObjectTreeAPIOfActiveSessionWindow();

      viewInObjectTree(objectName, objectTreeAPI);

   }

   public void viewInObjectTree(String objectName, IObjectTreeAPI objectTreeAPI)
   {
      ObjectTreeSearchCandidates candidates = getObjectCandidates(objectName);
      if (candidates.size() == 0)
      {
         return;
      }

      _viewInObjectTree(candidates, objectTreeAPI, true);
   }

   public void viewObjectInObjectTree(String objectName, IObjectTreeAPI objectTreeAPI)
   {
      ObjectTreeSearchCandidates candidates = getObjectCandidates(objectName);
      if (candidates.size() == 0)
      {
         return;
      }

      _viewInObjectTree(candidates, objectTreeAPI, false);

   }

   private void _viewInObjectTree(ObjectTreeSearchCandidates candidates, IObjectTreeAPI objectTreeAPI, boolean selectMainObjectTreeIfFound)
   {
      boolean success = false;
      while (candidates.hasNext())
      {

         ArrayList<String> catSchemObj = candidates.next();

         success = objectTreeAPI.selectInObjectTree(catSchemObj.get(0), catSchemObj.get(1), new FilterMatcher(catSchemObj.get(2), null));

         if (success && selectMainObjectTreeIfFound)
         {
            objectTreeAPI.getSession().selectMainTab(ISession.IMainPanelTabIndexes.OBJECT_TREE_TAB);
            break;
         }

      }

      if (false == success)
      {
         // i18n[ObjectTreeSearch.error.objectnotfound=Could not locate the database object ''{0}'' in Object tree]
         String msg = s_stringMgr.getString("ObjectTreeSearch.error.objectnotfound",candidates.getSearchString());
         JOptionPane.showMessageDialog(SessionUtils.getOwningFrame(objectTreeAPI.getSession()), msg);
      }
   }

   private ObjectTreeSearchCandidates getObjectCandidates(String objectName)
   {
      ObjectTreeSearchCandidates ret = new ObjectTreeSearchCandidates(objectName);

      String[] splits = objectName.split("\\.");


      for (int i = splits.length-1; i >=0 ; i--)
      {
         String object = null;
         String schema = null;
         String catalog = null;

         object = removeQuotes(splits[i]);

         if (i+1 < splits.length)
         {
            schema = splits[i+1];
         }

         if (i+2 < splits.length)
         {
            catalog = splits[i+2];
         }
         if (catalog == null && schema == null && "".equals(object)) {
             continue;
         }
         ret.add(catalog, schema, object);
      }

      return ret;
   }

   private String removeQuotes(String objectName)
   {
      String ret = objectName.trim();


      while(ret.startsWith("\"") || ret.startsWith("/"))
      {
         ret = ret.substring(1);
      }

      while(ret.endsWith("\"") || ret.endsWith("/"))
      {
          ret = ret.substring(0,ret.length()-1);
      }
      
      return ret;
   }

   public void viewInObjectTree(TreePath treePath, IObjectTreeAPI objectTreeAPI)
   {
      objectTreeAPI.selectInObjectTree(treePath);
      objectTreeAPI.getSession().selectMainTab(ISession.IMainPanelTabIndexes.OBJECT_TREE_TAB);
   }
}
