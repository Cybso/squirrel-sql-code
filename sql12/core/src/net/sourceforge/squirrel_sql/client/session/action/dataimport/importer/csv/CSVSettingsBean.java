package net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.csv;
/*
 * Copyright (C) 2007 Thorsten Mürell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * This class holds the configuration variables for the CSV importer.
 *
 * @author Thorsten Mürell
 */
public class CSVSettingsBean implements Cloneable, Serializable
{
   public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

   private Character seperator = ';';

   private  String importCharset = Charset.defaultCharset().name();

   private boolean useDoubleQuotesAsTextQualifier;


   private String dateFormat = DEFAULT_DATE_FORMAT;

   public CSVSettingsBean()
   {
   }

   /**
    * @return the importCharset
    */
   public String getImportCharset()
   {
      return importCharset;
   }

   /**
    * @param importCharset the importCharset to set
    */
   public void setImportCharset(String importCharset)
   {
      this.importCharset = importCharset;
   }

   /**
    * @return the seperator
    */
   public Character getSeperator()
   {
      return seperator;
   }

   /**
    * @param seperator the seperator to set
    */
   public void setSeperator(Character seperator)
   {
      this.seperator = seperator;
   }

   /**
    * @return the dateFormat
    */
   public String getDateFormat()
   {
      return dateFormat;
   }

   /**
    * @param dateFormat the dateFormat to set
    */
   public void setDateFormat(String dateFormat)
   {
      this.dateFormat = dateFormat;
   }

   public boolean isUseDoubleQuotesAsTextQualifier()
   {
      return useDoubleQuotesAsTextQualifier;
   }

   public void setUseDoubleQuotesAsTextQualifier(boolean useDoubleQuotesAsTextQualifier)
   {
      this.useDoubleQuotesAsTextQualifier = useDoubleQuotesAsTextQualifier;
   }
}
