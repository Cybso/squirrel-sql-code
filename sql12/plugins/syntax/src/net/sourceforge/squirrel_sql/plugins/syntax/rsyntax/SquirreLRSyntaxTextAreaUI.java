package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.SQLEntryPanelUtil;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IUndoHandler;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaDefaultInputMap;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaUI;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SquirreLRSyntaxTextAreaUI extends RSyntaxTextAreaUI
{
   private static final KeyStroke RS_KEY_STROKE_TO_UPPER_CASE = KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
   private static final KeyStroke RS_KEY_STROKE_TO_LOWER_CASE = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);

   private static final KeyStroke RS_KEY_STROKE_LINE_UP = KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
   private static final KeyStroke RS_KEY_STROKE_LINE_DOWN = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);

   private static final EditorKit _squirrel_defaultKit =
      new RSyntaxTextAreaEditorKit()
      {
         @Override
         public Action[] getActions()
         {
            return TextAction.augmentList(super.getActions(), new Action[]{new SQuirrelSelectWordAction()});
         }
      };


   private SquirrelRSyntaxTextArea _squirrelRSyntaxTextArea;

   public SquirreLRSyntaxTextAreaUI(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      super(squirrelRSyntaxTextArea);
      _squirrelRSyntaxTextArea = squirrelRSyntaxTextArea;
   }

   protected InputMap getRTextAreaInputMap()
   {
      // Except from modifiyKeystrokes() this is copied from RSyntaxTextAreaUI.
      // Not too nice

      InputMap map = new InputMapUIResource();
      InputMap shared = (InputMap)UIManager.get("RSyntaxTextAreaUI.inputMap");

      if (shared == null)
      {
         shared = new RSyntaxTextAreaDefaultInputMap();
         modifiyKeystrokes(shared);
         UIManager.put("RSyntaxTextAreaUI.inputMap", shared);
      }

      //KeyStroke[] keys = shared.allKeys();
      //for (int i=0; i<keys.length; i++)
      //	System.err.println(keys[i] + " -> " + shared.get(keys[i]));

      map.setParent(shared);
      return map;
   }

   private void modifiyKeystrokes(InputMap shared)
   {
      shared.remove(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK));
      shared.remove(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_DOWN_MASK));
      shared.remove(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
      shared.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK));
      shared.remove(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_DOWN_MASK));

      shared.remove(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK));
      shared.remove(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK));
      shared.remove(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
      shared.remove(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK)); // ChangeTrackAction

      /*
       *       Remove this Keystroke, because it triggers auto-complete of the current word with a matching most recent word,
       *       as long the shift and space key are pressed.
       *       See RTADefaultInputMap()
       *       put(KeyStroke.getKeyStroke(' '), RTextAreaEditorKit.rtaDumbCompleteWordAction); 
       */
      shared.remove(KeyStroke.getKeyStroke(' '));

      shared.put(getToUpperCaseKeyStroke(), RTextAreaEditorKit.rtaUpperSelectionCaseAction);
      shared.put(getToLowerCaseKeyStroke(), RTextAreaEditorKit.rtaLowerSelectionCaseAction);

      shared.put(getLineUpKeyStroke(), RTextAreaEditorKit.rtaLineUpAction);
      shared.put(getLineDownKeyStroke(), RTextAreaEditorKit.rtaLineDownAction);

      

      KeyStroke rsyntaxRedoStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
      KeyStroke squirrelRedoStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
      shared.put(squirrelRedoStroke,shared.get(rsyntaxRedoStroke));
   }

   public IUndoHandler createUndoHandler()
   {
      return new IUndoHandler()
      {
         @Override
         public Action getUndoAction()
         {
            return onGetUndoAction();
         }

         @Override
         public Action getRedoAction()
         {
            return onGetRedoAction();
         }
      };
   }

   private Action onGetUndoAction()
   {
      return getActionForName(_squirrelRSyntaxTextArea, RTextAreaEditorKit.rtaUndoAction);
   }

   private Action onGetRedoAction()
   {
      return getActionForName(_squirrelRSyntaxTextArea, RTextAreaEditorKit.rtaRedoAction);
   }

   public static Action getActionForName(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea, String actionName)
   {
      Action[] actions = squirrelRSyntaxTextArea.getUI().getEditorKit(squirrelRSyntaxTextArea).getActions();
      for (Action action : actions)
      {
         if(actionName.equals(action.getValue(Action.NAME)))
         {
            return action;
         }
      }
      throw new IllegalStateException("Action " + actionName + "not found");
   }

   public static KeyStroke getToUpperCaseKeyStroke()
   {
      return KeyStroke.getKeyStroke(Main.getApplication().getShortcutManager().registerAccelerator(RTextAreaEditorKit.rtaUpperSelectionCaseAction, SquirreLRSyntaxTextAreaUI.RS_KEY_STROKE_TO_UPPER_CASE));
   }

   public static KeyStroke getToLowerCaseKeyStroke()
   {
      return KeyStroke.getKeyStroke(Main.getApplication().getShortcutManager().registerAccelerator(RTextAreaEditorKit.rtaLowerSelectionCaseAction, SquirreLRSyntaxTextAreaUI.RS_KEY_STROKE_TO_LOWER_CASE));
   }

   public static KeyStroke getLineUpKeyStroke()
   {
      return KeyStroke.getKeyStroke(Main.getApplication().getShortcutManager().registerAccelerator(RTextAreaEditorKit.rtaLineUpAction, SquirreLRSyntaxTextAreaUI.RS_KEY_STROKE_LINE_UP));
   }

   public static KeyStroke getLineDownKeyStroke()
   {
      return KeyStroke.getKeyStroke(Main.getApplication().getShortcutManager().registerAccelerator(RTextAreaEditorKit.rtaLineDownAction, SquirreLRSyntaxTextAreaUI.RS_KEY_STROKE_LINE_DOWN));
   }




   @Override
   public EditorKit getEditorKit(JTextComponent tc)
   {
      return _squirrel_defaultKit;
   }

   private static class SQuirrelSelectWordAction extends RSyntaxTextAreaEditorKit.SelectWordAction
   {
      public void actionPerformedImpl(ActionEvent e, RTextArea textArea)
      {
         int[] wordBoundsAtCursor = SQLEntryPanelUtil.getWordBoundsAtCursor(textArea, false);
         textArea.setSelectionStart(wordBoundsAtCursor[0]);
         textArea.setSelectionEnd(wordBoundsAtCursor[1]);
      }
   }
}
