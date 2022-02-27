package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2003-2004 Jason Height
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.QueryHolder;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;

/**
 * This default implementation of the sql executer handler simply notifies the
 * message handler when events occur.
 *
 * @author  <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public class DefaultSQLExecuterHandler implements ISQLExecuterHandler
{
	private ISession _session;

	public DefaultSQLExecuterHandler(ISession session)
	{
		_session = session;
	}

	public void sqlToBeExecuted(QueryHolder queryHolder)
	{
	}

	public void sqlExecutionCancelled()
	{
	}

	public void sqlDataUpdated(int updateCount)
	{
	}

	public void sqlResultSetAvailable(ResultSetWrapper rst, SQLExecutionInfo info, IDataSetUpdateableTableModel model)
	{
	}

	public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount)
	{
	}

	public String sqlExecutionException(Throwable th, String postErrorString)
	{
      String msg = "Error: ";

      if(th instanceof SQLException)
      {
         SQLException sqlEx = (SQLException) th;
			String msg1 = Utilities.getSqlExecutionErrorMessage(sqlEx);

			msg += msg1;
      }
      else
      {
         msg += th;
      }

      if(null != postErrorString)
      {
         msg += "\n" + postErrorString;
      }

      _session.showErrorMessage(msg);
      return msg;
   }

	public void sqlExecutionWarning(SQLWarning warn)
	{
		_session.showMessage(warn);
	}

   public void sqlStatementCount(int statementCount)
   {
   }

   public void sqlCloseExecutionHandler(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
   {
   }
}