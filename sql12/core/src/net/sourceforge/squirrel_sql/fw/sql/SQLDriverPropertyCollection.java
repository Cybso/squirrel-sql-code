package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2003 Colin Bell
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

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasVersioner;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.DriverPropertyInfo;
import java.util.*;
/**
 * A collection of <TT>SQLDriverDriverProperty</TT> objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLDriverPropertyCollection implements Serializable
{
	/**
	 * JavaBean property names for this class.
	 */
	public interface IPropertyNames
	{
		String DRIVER_PROPERTIES = "driverProperties";
	}

	/** Collection of <TT></TT> objects keyed by the object name. */
	private final Map<String, SQLDriverProperty> _objectsIndexMap = new TreeMap<>();

	/** Array of <TT>SQLDriverProperty</TT> objects. */
	private final List<SQLDriverProperty> _objectsList = new ArrayList<>();

	private SQLAliasVersioner _versioner = new SQLAliasVersioner();

	public SQLDriverPropertyCollection()
	{
	}

	/**
	 * Clear all entries from this collection.
	 */
	public void clear()
	{
		_objectsIndexMap.clear();
		_objectsList.clear();
	}

	/**
	 * Retrieve the number of elements in this collection.
	 *
	 * @return	the number of elements in this collection.
	 */
	public int size()
	{
		return _objectsList.size();
	}

	public void applyTo(Properties props)
	{
		for (int i = 0, limit = size(); i < limit; ++i)
		{
			SQLDriverProperty sdp = getDriverProperty(i);
			if (sdp.isSpecified())
			{
				final String value = sdp.getValue();
				if (value != null)
				{
					props.put(sdp.getName(), value);
				}
			}
		}
	}

	/**
	 * Retrieve an array of the <TT>SQLDriverProperty</TT> objects contained
	 * in this collection.
	 *
	 * @return	an array of the <TT>SQLDriverProperty</TT> objects contained
	 *			in this collection.
	 */
	public SQLDriverProperty[] getDriverProperties()
	{
		SQLDriverProperty[] ar = new SQLDriverProperty[_objectsList.size()];
		return _objectsList.toArray(ar);
	}

	public SQLDriverProperty getDriverProperty(int idx)
	{
		return _objectsList.get(idx);
	}

	public SQLDriverProperty getDriverPropertyByName(String name)
	{
		return _objectsList.stream().filter(p -> StringUtils.equals(p.getName(), name)).findFirst().orElse(null);
	}


	public void setDriverProperties(SQLDriverProperty[] values)
	{
		_objectsIndexMap.clear();
		_objectsList.clear();
		for (int i = 0; i < values.length; ++i)
		{
			_objectsList.add(values[i]);
			_objectsIndexMap.put(values[i].getName(), values[i]);
		}
	}

	public void addDriverProperty(SQLDriverProperty value) {
		_objectsList.add(value);
		_objectsIndexMap.put(value.getName(), value);		
	}
	
	public void removeDriverProperty(String name) {
		SQLDriverProperty prop = _objectsIndexMap.remove(name);
		_objectsList.remove(prop);
	}
	
	/**
	 * Warning - should only be used when loading javabean from XML.
	 */
	public void setDriverProperty(int idx, SQLDriverProperty value)
	{
		_objectsList.add(idx, value);
		_objectsIndexMap.put(value.getName(), value);
	}

	public void applyDriverPropertyInfo(DriverPropertyInfo[] infoAr)
	{
		if (infoAr == null || infoAr.length == 0)
		{
			infoAr = new DriverPropertyInfo[1];
            infoAr[0] = new DriverPropertyInfo("remarksReporting", "true");
            infoAr[0].required = false;
            infoAr[0].description = "Set to true in order to table/column comments";
		}
		for (int i = 0; i < infoAr.length; ++i)
		{
			SQLDriverProperty sdp = _objectsIndexMap.get(infoAr[i].name);
			if (sdp == null)
			{
				sdp = new SQLDriverProperty(infoAr[i]);
				_objectsIndexMap.put(sdp.getName(), sdp);
				_objectsList.add(sdp);
			}
			sdp.setDriverPropertyInfo(infoAr[i]);
		}
	}

	public void acceptAliasVersioner(SQLAliasVersioner versioner)
	{
		_versioner = versioner;
	}

	public SQLAliasVersioner getVersioner()
	{
		return _versioner;
	}
}
