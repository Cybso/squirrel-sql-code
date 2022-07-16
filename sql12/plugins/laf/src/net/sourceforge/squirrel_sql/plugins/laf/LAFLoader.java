package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.fw.util.SquirrelURLClassLoader;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

import javax.swing.LookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class LAFLoader
{
   private static ILogger s_log = LoggerController.createLogger(LAFLoader.class);

   public static LookAndFeel getLafOrDefaultMetalOnError(LAFPreferences prefs, SquirrelURLClassLoader lafClassLoader)
   {
      return _getLookAndFeel(prefs.getLookAndFeelClassName(), lafClassLoader, false);
   }


   public static LookAndFeel getLafOrNullOnError(String lafClassName, SquirrelURLClassLoader lafClassLoader)
   {
      return _getLookAndFeel(lafClassName, lafClassLoader, true);
   }


   private static LookAndFeel _getLookAndFeel(String lookAndFeelClassName, SquirrelURLClassLoader lafClassLoader, boolean silentAndNullable)
   {
      LookAndFeel laf = new MetalLookAndFeel();

      if (false == MetalLookAndFeel.class.getName().equals(lookAndFeelClassName))
      {
         Class<?> lafClass;
         try
         {
            if (lafClassLoader != null)
            {
               lafClass = Class.forName(lookAndFeelClassName, true, lafClassLoader);
            }
            else
            {
               lafClass = Class.forName(lookAndFeelClassName);
            }

            laf = (LookAndFeel) lafClass.getDeclaredConstructor().newInstance();
         }
         catch (Throwable t)
         {
            if (silentAndNullable)
            {
               return null;
            }
            else
            {
               String jgoodiesWindowsLafErr =
                     "superclass access check failed: class com.jgoodies.looks.windows.WindowsLookAndFeel";

               if (StringUtils.containsIgnoreCase(t.toString(), jgoodiesWindowsLafErr))
               {
                  String jgoodiesMsg = "JGoodies WindowsLookAndFeel Java 17 error:\n" +
                        "Failed to load JGoodies WindowsLookAndFeel because it uses an internal com.sun API which from Java 17 on isn't accessible anymore.\n" +
                        "For a workaround see bug #1507 at SourceForge: " +
                        "https://sourceforge.net/p/squirrel-sql/bugs/1507\n" +
                        "Note that JGoodies does not offer open source updates for its Look and Feels anymore, see: http://www.jgoodies.com/downloads/libraries/\n" +
                        "Detailed error message:";
                  s_log.error(jgoodiesMsg, t);
               }
               else
               {
                  s_log.error("Failed to load Look and Feel class switching to Metal with Ocean theme.", t);
               }
            }
         }
      }

      return laf;
   }
}
