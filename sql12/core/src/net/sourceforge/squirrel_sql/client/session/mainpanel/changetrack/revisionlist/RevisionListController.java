package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackCloseDispatcher;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.ChangeTrackCloseListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.GitHandler;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

public class RevisionListController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RevisionListController.class);

   private static final String PREF_KEY_SPLIT_DIVIDER_LOCATION = "changetrack.RevisionListController.split.divider.location";


   private RevisionListDialog _dlg;
   private ChangeTrackCloseDispatcher _changeTrackCloseDispatcher;
   private RevisionListControllerListener _revisionListControllerListener;
   private File _file;
   private ChangeTrackCloseListener _changeTrackCloseListener;

   public RevisionListController(JComponent parentComp,
                                 ChangeTrackCloseDispatcher changeTrackCloseDispatcher,
                                 RevisionListControllerListener revisionListControllerListener,
                                 File file)
   {
      _file = file;
      _dlg = new RevisionListDialog(parentComp, _file.getName(), GitHandler.getPathRelativeToRepo(file), GitHandler.getFilesRepositoryWorkTreePath(file));

      _changeTrackCloseDispatcher = changeTrackCloseDispatcher;
      _revisionListControllerListener = revisionListControllerListener;
      _changeTrackCloseListener = () -> onChangeTrackClosed();
      _changeTrackCloseDispatcher.addChangeTrackCloseListener(_changeTrackCloseListener);


      List<RevisionWrapper> revisions = GitHandler.getRevisions(file);

      _dlg.lstRevisions.setListData(revisions.toArray(new RevisionWrapper[0]));
      _dlg.lstRevisions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      if(0 < revisions.size())
      {
         _dlg.lstRevisions.setSelectedIndex(0);
         _dlg.lstRevisions.ensureIndexIsVisible(0);
      }

      _dlg.lstRevisions.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            maybeShowRevisionListPopup(e);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            maybeShowRevisionListPopup(e);
         }
      });

      _dlg.txtPreview.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            maybeShowPreviewPopup(e);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            maybeShowPreviewPopup(e);
         }
      });

      GUIUtils.initLocation(_dlg, 500, 500);
      GUIUtils.enableCloseByEscape(_dlg, dialog -> saveSplitLocation());

      initSplitDividerLocation();


      _dlg.lstRevisions.addListSelectionListener(e -> onListSelectionChanged(e));
      onListSelectionChanged(null);

      _dlg.addWindowListener(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent e)
         {
            saveSplitLocation();
         }

         @Override
         public void windowClosed(WindowEvent e)
         {
            saveSplitLocation();
         }
      });

      _dlg.setVisible(true);

   }

   private void maybeShowPreviewPopup(MouseEvent me)
   {
      if(false == me.isPopupTrigger())
      {
         return;
      }

      JPopupMenu popupMenu = new JPopupMenu();

      JMenuItem mnuPreviewCopy = new JMenuItem(s_stringMgr.getString("RevisionListController.preview.copy"));
      mnuPreviewCopy.addActionListener(e -> ClipboardUtil.copyToClip(_dlg.txtPreview.getSelectedText(), true));
      popupMenu.add(mnuPreviewCopy);

      JMenuItem mnuPreviewCopyAll = new JMenuItem(s_stringMgr.getString("RevisionListController.preview.copy.all"));
      mnuPreviewCopyAll.addActionListener(e -> ClipboardUtil.copyToClip(_dlg.txtPreview.getText(), true));
      popupMenu.add(mnuPreviewCopyAll);

      popupMenu.show(_dlg.txtPreview, me.getX(), me.getY());

   }

   private void onChangeTrackClosed()
   {
      _changeTrackCloseDispatcher.removeChangeTrackCloseListener(_changeTrackCloseListener);
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void saveSplitLocation()
   {
      Main.getApplication().getPropsImpl().put(PREF_KEY_SPLIT_DIVIDER_LOCATION, _dlg.splitTreePreview.getDividerLocation());
   }

   private void onListSelectionChanged(ListSelectionEvent e)
   {
      if(null != e && e.getValueIsAdjusting())
      {
         return;
      }

      _dlg.txtPreview.setText(null);

      RevisionWrapper selectedWrapper = _dlg.lstRevisions.getSelectedValue();

      if(null == selectedWrapper)
      {
         return;
      }

      String fileContent = GitHandler.getVersionOfFile(_file, selectedWrapper.getRevCommitId(), selectedWrapper.getPreviousNamesOfFileRelativeToRepositoryRoot());

      _dlg.txtPreview.setText(fileContent);

      SwingUtilities.invokeLater(() -> _dlg.txtPreview.scrollRectToVisible(new Rectangle(0,0,1,1)));
   }

   private void initSplitDividerLocation()
   {
      int preferredDividerLocation = Main.getApplication().getPropsImpl().getInt(PREF_KEY_SPLIT_DIVIDER_LOCATION, _dlg.getWidth() / 2);

      int dividerLocation = preferredDividerLocation;
      if (0 < _dlg.splitTreePreview.getWidth())
      {
         dividerLocation = Math.min(_dlg.splitTreePreview.getMaximumDividerLocation(), preferredDividerLocation);
      }

      _dlg.splitTreePreview.setDividerLocation(dividerLocation);
   }

   private void maybeShowRevisionListPopup(MouseEvent me)
   {
      if(false == me.isPopupTrigger())
      {
         return;
      }

      int ix = _dlg.lstRevisions.locationToIndex(me.getPoint());

      if(ix < 0)
      {
         return;
      }

      _dlg.lstRevisions.setSelectedIndex(ix);

      JPopupMenu popupMenu = new JPopupMenu();

      JMenu mnuCopy = new JMenu(s_stringMgr.getString("RevisionListController.copy"));

      RevisionWrapper selectedWrapper = _dlg.lstRevisions.getSelectedValue();
      mnuCopy.add(createCopyMenu("RevisionListController.copy.all", selectedWrapper.getDisplayString()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.date", selectedWrapper.getRevisionDateString()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.brancheslist", selectedWrapper.getBranchesListString()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.user", selectedWrapper.getCommitterName()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.revid", selectedWrapper.getRevisionIdString()));
      mnuCopy.add(createCopyMenu("RevisionListController.copy.commitMsg", selectedWrapper.getCommitMsg()));

      popupMenu.add(mnuCopy);


      JMenuItem mnuAsChangeTrackBase = new JMenuItem(s_stringMgr.getString("RevisionListController.as.change.track.base"));
      mnuAsChangeTrackBase.addActionListener(e -> _revisionListControllerListener.replaceChangeTrackBase(_dlg.txtPreview.getText()));
      popupMenu.add(mnuAsChangeTrackBase);

      JMenuItem mnuAsEditorContent = new JMenuItem(s_stringMgr.getString("RevisionListController.as.editor.content"));
      mnuAsEditorContent.addActionListener(e -> _revisionListControllerListener.replaceEditorContent(_dlg.txtPreview.getText()));
      popupMenu.add(mnuAsEditorContent);

      popupMenu.show(_dlg.lstRevisions, me.getX(), me.getY());
   }

   private JMenuItem createCopyMenu(String title, String toCopy)
   {
      JMenuItem ret = new JMenuItem(s_stringMgr.getString(title));

      ret.addActionListener(e -> ClipboardUtil.copyToClip(toCopy));

      return ret;
   }
}
