package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.MarkDuplicatesToggleAction;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ButtonChooser;
import net.sourceforge.squirrel_sql.fw.gui.table.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import java.awt.event.ActionEvent;

public class MarkDuplicatesChooserController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MarkDuplicatesChooserController.class);
   private final IDataSetViewer _sqlResultDataSetViewer;

   private ButtonChooser _toggleBtnChooser;

   private static final String PREF_MARK_DUPLICATES_MODE_LAST_MODE = "MarkDuplicatesMode.last.mode";

   private boolean _dontReactToEvents = false;


   public MarkDuplicatesChooserController(IDataSetViewer dataSetViewer)
   {
      this(dataSetViewer, null);
   }

   public MarkDuplicatesChooserController(IResultTab resultTab)
   {
      this(resultTab.getSQLResultDataSetViewer(), resultTab);
   }

   private MarkDuplicatesChooserController(IDataSetViewer dataSetViewer, IResultTab resultTab)
   {
      _sqlResultDataSetViewer = dataSetViewer;
      _toggleBtnChooser = new ButtonChooser();

      for (MarkDuplicatesMode mode : MarkDuplicatesMode.values())
      {
         JToggleButton btn;
         if(null != resultTab)
         {
            // MarkDuplicatesToggleAction results in call of actionWasFired(...)
            btn = new JToggleButton(new MarkDuplicatesToggleAction(resultTab));
         }
         else
         {
            btn = new JToggleButton();
            btn.addActionListener(e -> actionWasFired(e));
         }

         btn.setIcon(mode.getIcon());
         btn.setText(mode.getText());
         btn.setToolTipText(mode.getToolTipText());
         mode.assignModeToButton(btn);
         _toggleBtnChooser.addButton(btn);
      }

      _toggleBtnChooser.setSelectedButton(getLastMode().findButton(_toggleBtnChooser));

      _toggleBtnChooser.setButtonSelectedListener((newSelectedButton, formerSelectedButton) -> onButtonSelected((JToggleButton)newSelectedButton, (JToggleButton)formerSelectedButton));

      if(_sqlResultDataSetViewer instanceof DataSetViewerTablePanel)
      {
         SortableTableModel sortableTableModel = ((DataSetViewerTablePanel) _sqlResultDataSetViewer).getTable().getSortableTableModel();
         sortableTableModel.addSortingListener((modelColumnIx, columnOrder) -> onTableSorted());
      }

      if(_sqlResultDataSetViewer instanceof DataSetViewerTablePanel)
      {
         ((DataSetViewerTablePanel) _sqlResultDataSetViewer).getTable().getButtonTableHeader().setDraggedColumnListener(() -> onColumnMoved());
      }
   }

   private void onColumnMoved()
   {
      AbstractButton selButton = _toggleBtnChooser.getSelectedButton();
      if(false == selButton.isSelected())
      {
         return;
      }

      MarkDuplicatesMode selectedMode = MarkDuplicatesMode.getModeByButton(selButton);
      if(   selectedMode == MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_CELLS_IN_ROW
         || selectedMode == MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_VALUES_IN_COLUMNS)
      {
         try
         {
            _dontReactToEvents = true;
            selButton.setSelected(false);
         }
         finally
         {
            _dontReactToEvents = false;
         }

         doMarkDuplicates();
      }
   }

   private void onTableSorted()
   {
      AbstractButton selButton = _toggleBtnChooser.getSelectedButton();
      if(false == selButton.isSelected())
      {
         return;
      }

      MarkDuplicatesMode selectedMode = MarkDuplicatesMode.getModeByButton(selButton);
      if( selectedMode == MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_VALUES_IN_COLUMNS || selectedMode == MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_ROWS)
      {
         try
         {
            _dontReactToEvents = true;

            selButton.setSelected(false);
         }
         finally
         {
            _dontReactToEvents = false;
         }

         doMarkDuplicates();
      }
   }

   private void onButtonSelected(final JToggleButton newSelectedButton, JToggleButton formerSelectedButton)
   {
      if(_dontReactToEvents)
      {
         return;
      }

      Props.putString(PREF_MARK_DUPLICATES_MODE_LAST_MODE, MarkDuplicatesMode.getModeByButton(newSelectedButton).name());

      try
      {
         _dontReactToEvents = true;
         newSelectedButton.setSelected(formerSelectedButton.isSelected());

         // Needed because of the way GUIUtils.styleAsToolbarButton() works.
         newSelectedButton.setContentAreaFilled(newSelectedButton.isSelected());
      }
      finally
      {
         _dontReactToEvents = false;
      }

      doMarkDuplicates();
   }


   private MarkDuplicatesMode getLastMode()
   {
      return MarkDuplicatesMode.valueOf(Props.getString(PREF_MARK_DUPLICATES_MODE_LAST_MODE, MarkDuplicatesMode.DUPLICATE_VALUES_IN_COLUMNS.name()));
   }


   public JComponent getComponent()
   {
      return _toggleBtnChooser.getComponent();
   }

   public boolean actionWasFired(ActionEvent e)
   {
      if(_dontReactToEvents)
      {
         return true;
      }

      if(e.getSource() != _toggleBtnChooser.getSelectedButton())
      {
         // Happens through shortcut or menu.
         _toggleBtnChooser.getSelectedButton().doClick();
         return false;
      }

      doMarkDuplicates();
      return true;
   }

   private void doMarkDuplicates()
   {

      if(false == _sqlResultDataSetViewer instanceof DataSetViewerTablePanel)
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("MarkDuplicatesChooserController.mark.duplicates.for.table.output.only"));
         return;
      }

      boolean selected = _toggleBtnChooser.getSelectedButton().isSelected();

      MarkDuplicatesMode mode = null;

      if(selected)
      {
         mode = MarkDuplicatesMode.getModeByButton(_toggleBtnChooser.getSelectedButton());
      }

      ((DataSetViewerTablePanel) _sqlResultDataSetViewer).getTable().getColoringService().getMarkDuplicatesHandler().markDuplicates(mode);
   }

   public void copyStateFrom(MarkDuplicatesChooserController controllerToCopyFrom)
   {
      try
      {
         _dontReactToEvents = true;

         for (AbstractButton button : _toggleBtnChooser.getAllButtons())
         {
            if(button.getIcon() == controllerToCopyFrom._toggleBtnChooser.getSelectedButton().getIcon())
            {
               _toggleBtnChooser.setSelectedButton(button);
               button.setSelected(controllerToCopyFrom._toggleBtnChooser.getSelectedButton().isSelected());
               break;
            }
         }

         // Needed because of the way GUIUtils.styleAsToolbarButton() works.
         _toggleBtnChooser.getSelectedButton().setContentAreaFilled(_toggleBtnChooser.getSelectedButton().isSelected());

      }
      finally
      {
         _dontReactToEvents = false;
      }
   }

}
