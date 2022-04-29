package net.sourceforge.squirrel_sql.plugins.graph.link;

import net.sourceforge.squirrel_sql.client.session.DataModelImplementationDetails;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionUtils;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPlugin;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class LinkGraphDialog extends JDialog
{
   private static final String PREF_KEY_LINK_SHEET_WIDTH = "Squirrel.graph.link.dlg.width";
   private static final String PREF_KEY_LINK_SHEET_HEIGHT = "Squirrel.graph.link.dlg.height";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(LinkGraphDialog.class);
   DataSetViewerTablePanel tblGraphFiles;

   JTextField txtDir;

   JButton btnExplore;
   JButton btnChangeDir;
   JButton btnHomeDir;
   JButton btnCreate;


   public LinkGraphDialog(GraphPlugin graphPlugin, ISession session)
   {
      super(SessionUtils.getOwningFrame(session), s_stringMgr.getString("linkGraph.dialog.title", session.getAlias().getUrl()));

      JPanel content = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
      String lblText = s_stringMgr.getString("linkGraph.dialog.displayedFolder", session.getAlias().getUrl());
      content.add(new MultipleLineLabel(lblText), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0);
      txtDir = new JTextField();
      txtDir.setEditable(false);
      content.add(txtDir, gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      content.add(createFolderButtonsPanel(), gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 5, 5), 0, 0);
      content.add(new JLabel(s_stringMgr.getString("linkGraph.dialog.graphsInFolder")), gbc);

      gbc = new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
      tblGraphFiles = new DataSetViewerTablePanel();
      tblGraphFiles.init(null, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, new DataModelImplementationDetails(session), session);
      content.add(new JScrollPane(tblGraphFiles.getComponent()), gbc);

      gbc = new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
      content.add(createButtonPanel(session, content), gbc);

      getContentPane().setLayout(new GridLayout(1, 1));
      getContentPane().add(content);

      setSize(getDimension());

      GUIUtils.centerWithinParent(this);
      GUIUtils.enableCloseByEscape(this);

      setVisible(true);

      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onWindowClosing();
         }
      });
   }

   private JPanel createButtonPanel(ISession session, JPanel content)
   {
      JPanel ret = new JPanel(new GridBagLayout());
      
      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0);
      btnCreate = new JButton(s_stringMgr.getString("linkGraph.dialog.btnCreate"));
      ret.add(btnCreate, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 3, 5, 5), 0, 0);
      ret.add(new JLabel(s_stringMgr.getString("linkGraph.dialog.createLinksLabel", session.getAlias().getUrl())), gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0);
      ret.add(new JPanel(), gbc);

      return ret;

   }

   private void onWindowClosing()
   {
      Dimension size = getSize();
      Props.putInt(PREF_KEY_LINK_SHEET_WIDTH, size.width);
      Props.putInt(PREF_KEY_LINK_SHEET_HEIGHT, size.height);
   }

   private JPanel createFolderButtonsPanel()
   {
      JPanel pnl = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
      btnChangeDir = new JButton(s_stringMgr.getString("linkGraph.dialog.changeFolder"));
      pnl.add(btnChangeDir, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
      btnHomeDir = new JButton(s_stringMgr.getString("linkGraph.dialog.toHomeDir"));
      pnl.add(btnHomeDir, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0);
      btnExplore = new JButton(s_stringMgr.getString("linkGraph.dialog.openFolderInExplorer"));
      pnl.add(btnExplore, gbc);

      return pnl;

   }

   private Dimension getDimension()
   {
      return new Dimension(
            Props.getInt(PREF_KEY_LINK_SHEET_WIDTH, 800),
            Props.getInt(PREF_KEY_LINK_SHEET_HEIGHT, 500)
      );
   }


}
