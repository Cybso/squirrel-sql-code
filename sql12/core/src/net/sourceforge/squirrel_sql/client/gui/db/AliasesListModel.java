package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.SortedListModel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;

import java.util.Arrays;
import java.util.Iterator;

/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
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
/**
 * Model for an <TT>AliasesList</TT> object.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AliasesListModel extends SortedListModel<ISQLAlias>
{
    /** Application API. */
	private IApplication _app;

	private boolean _isBeingSortedForListImpl = false;

	/**
	 * Listen to the <TT>DataCache</TT> object for additions
	 * and removals of aliases from the cache.
	 *
	 * @param   app	 Application API.
	 */
	public AliasesListModel(IApplication app)
	{
		super();
		_app = app;
		load();
		_app.getAliasesAndDriversManager().addAliasesListener(new MyAliasesListener());
	}

	/**
	 * Load from <TT>DataCache</TT>.
	 */
	private void load()
	{
		Iterator<? extends ISQLAlias> it = _app.getAliasesAndDriversManager().aliases();
		while (it.hasNext())
		{
			addAlias(it.next());
		}
	}

	/**
	 * Add an <TT>ISQLAlias</TT> to this model.
	 *
	 * @param   alias   <TT>ISQLAlias</TT> to be added.
	 */
	private void addAlias(ISQLAlias alias)
	{
		addElement(alias);
	}

	/**
	 * Remove an <TT>ISQLAlias</TT> from this model.
	 *
	 * @param   alias   <TT>ISQLAlias</TT> to be removed.
	 */
	private void removeAlias(ISQLAlias alias)
	{
		removeElement(alias);
	}

   public void sortAliasesForListImpl()
   {
		try
		{
			_isBeingSortedForListImpl = true;
			Object[] aliases = toArray();

			Arrays.sort(aliases);

			clear();

			for (int i = 0; i < aliases.length; i++)
         {
            addElement((ISQLAlias) aliases[i]);
			}
		}
		finally
		{
			_isBeingSortedForListImpl = false;
		}
	}

   public int getIndex(SQLAlias alias)
   {
      for (int i = 0; i < size(); i++)
      {
         if (get(i).equals(alias))
         {
            return i;
         }
      }

      return -1;
   }

   public SQLAlias getAlias(IIdentifier aliasIdentifier)
   {
      for (int i = 0; i < size(); i++)
      {
         SQLAlias alias = (SQLAlias) get(i);

         if (aliasIdentifier.equals(alias.getIdentifier()))
         {
            return alias;
         }
      }

      return null;
   }

	public boolean isBeingSortedForListImpl()
	{
		return _isBeingSortedForListImpl;
	}


	/**
	 * Listener to changes in <TT>ObjectCache</TT>. As aliases are
	 * added to/removed from <TT>DataCache</TT> this model is updated.
	 */
	private class MyAliasesListener implements IObjectCacheChangeListener
	{
		/**
		 * An alias has been added to the cache.
		 *
		 * @param   evt	 Describes the event in the cache.
		 */
		public void objectAdded(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				addAlias((ISQLAlias)obj);
			}
		}

		/**
		 * An alias has been removed from the cache.
		 *
		 * @param   evt	 Describes the event in the cache.
		 */
		public void objectRemoved(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				removeAlias((ISQLAlias)obj);
			}
		}
	}
}
