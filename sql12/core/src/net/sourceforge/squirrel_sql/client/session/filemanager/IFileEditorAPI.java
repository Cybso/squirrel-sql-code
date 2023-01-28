package net.sourceforge.squirrel_sql.client.session.filemanager;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.JTextArea;
import java.awt.Frame;

public interface IFileEditorAPI
{
   /**
    * former {@link SQLPanelSelectionHandler#selectSqlPanel(ISQLPanelAPI)}
    */
   void selectWidgetOrTab();

   Frame getOwningFrame();

   /**
    * Append the passed SQL script to the SQL entry area and specify
    * whether it should be selected.
    *
    * @param	sqlScript	The script to be appended.
    * @param	select		If <TT>true</TT> then select the passed script
    * 						in the sql entry area.
    */
   void appendSQLScript(String sqlScript, boolean select);

   /**
    * Replace the contents of the SQL entry area with the passed
    * SQL script without selecting it.
    *
    * @param	sqlScript	The script to be placed in the SQL entry area.
    */
   void setEntireSQLScript(String sqlScript);

   ISession getSession();

   /**
    * Return the entire contents of the SQL entry area.
    *
    * @return	the entire contents of the SQL entry area.
    */
   String getEntireSQLScript();

   /**
    * Just a rename. Inline if you care.
    */
   default String getText()
   {
      return getEntireSQLScript();
   }

   int getCaretPosition();


   void setCaretPosition(int caretPos);

   JTextArea getTextComponent();

   FileHandler getFileHandler();

   ISQLPanelAPI getSQLPanelAPIOrNull();
}
