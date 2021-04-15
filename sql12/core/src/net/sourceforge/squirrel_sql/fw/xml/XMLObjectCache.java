package net.sourceforge.squirrel_sql.fw.xml;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.IObjectCache;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCache;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
/**
 * This class is a cache of objects that can be read from/written to an XML
 * document. All objects stored must implement <CODE>IHasIdentifier</CODE>.<P>
 *
 * It is implemented using <CODE>ObjectCache</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class XMLObjectCache<E extends IHasIdentifier> implements IObjectCache<E>
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(XMLObjectCache.class);

	/** Cache of stored objects. */
	private ObjectCache<E> _cache = new ObjectCache<E>();

	public XMLObjectCache()
	{
	}

	/**
	 * Retrieve a stored object.
	 *
	 * @param	objClass	The class of the object to be retrieved.
	 * @param	id			The <CODE>IIdentifier</CODE> that identifies
	 *						the object to be retrieved.
	 *
	 * @return	The <CODE>IHasIdentifier</CODE> retrieved or <CODE>null</CODE>
	 *			if no object exists for <CODE>id</CODE>.
	 */
	public IHasIdentifier get(Class<E> objClass, IIdentifier id)
	{
		return _cache.get(objClass, id);
	}

	/**
	 * Store an object.
	 *
	 * @param	obj	Object to be stored.
	 *
	 * @exception	DuplicateObjectException
	 *				Thrown if an object of the same class as <CODE>obj</CODE>
	 *				and with the same identifier is already in the cache.
	 */
	public void add(E obj) throws DuplicateObjectException
	{
		_cache.add(obj);
	}

	/**
	 * Remove an object.
	 *
	 * @param	objClass	Class of object to be removed.
	 * @param	id			Identifier for object to be removed.
	 */
	public void remove(Class<E> objClass, IIdentifier id)
	{
		_cache.remove(objClass, id);
	}

	/**
	 * Return an array of <CODE>Class</CODE objects that represent all the
	 * different types of objects stored.
	 *
	 * @return	Class[] of all classes stored.
	 */
	public Class<E>[] getAllClasses()
	{
		return _cache.getAllClasses();
	}

	/**
	 * Return an <CODE>Iterator</CODE> of all objects stored for the
	 * passed class.
	 *
	 * @param	objClass	Class to return objects for.
	 *
	 * @return	<CODE>Iterator</CODE> over all objects.
	 */
	public Iterator<E> getAllForClass(Class<E> objClass)
	{
		return _cache.getAllForClass(objClass);
	}

	@Override
	public List<E> getAllForClassAsList(Class<E> objClass)
	{
		return _cache.getAllForClassAsList(objClass);
	}


	/**
	 * Adds a listener for changes to the cache entry for the passed class.
	 *
	 * @param	lis			an IObjectCacheChangeListener that will be notified
	 *						when objects are added and removed from this cache
	 *						entry.
	 * @param	objClass	The class of objects whose cache we want to listen
	 *						to.
	 */
	public void addChangesListener(IObjectCacheChangeListener lis, Class<E> objClass)
	{
		_cache.addChangesListener(lis, objClass);
	}

	/**
	 * Removes a listener for changes to the cache entry for the passed class.
	 *
	 * @param	lis			an IObjectCacheChangeListener that will be notified
	 *						when objects are added and removed from this cache
	 *						entry.
	 * @param	objClass	The class of objects whose cache we want to listen
	 *						to.
	 */
	public void removeChangesListener(IObjectCacheChangeListener lis,
										Class<E> objClass)
	{
		_cache.removeChangesListener(lis, objClass);
	}

	/**
	 * Load from an XML document.
	 *
	 * @param	xmlFileName	Name of XML file to load from.
	 *
	 * @exception	FileNotFoundException
	 *				Thrown if file not found.
	 *
	 * @exception	XMLException
	 *				Thrown if an XML error occurs.
	 *
	 * @exception	DuplicateObjectException
	 *				Thrown if two objects of the same class
	 *				and with the same identifier are added to the cache.
	 */
	public void load(String xmlFileName)
		throws FileNotFoundException, XMLException, DuplicateObjectException
	{
		load(xmlFileName, null);
	}

	/**
	 * Load from an XML document but don't ignore duplicate objects.
	 *
	 * @param	xmlFileName	Name of XML file to load from.
	 * @param	cl			Class loader to use for object creation.
	 *
	 * @exception	FileNotFoundException
	 *				Thrown if file not found.
	 *
	 * @exception	XMLException
	 *				Thrown if an XML error occurs.
	 *
	 * @exception	DuplicateObjectException
	 *				Thrown if two objects of the same class
	 *				and with the same identifier are added to the cache.
	 */
	public void load(String xmlFileName, ClassLoader cl)
		throws FileNotFoundException, XMLException, DuplicateObjectException
	{
		XMLBeanReader rdr = new XMLBeanReader();
		rdr.load(xmlFileName, cl);
		for (Iterator<Object> it = rdr.iterator(); it.hasNext();)
		{
			final Object obj = it.next();
			if (!(obj instanceof IHasIdentifier))
			{
				throw new XMLException(s_stringMgr.getString("XMLObjectCache.error.notimplemented"));
			}
			add((E)obj);
		}
	}

	/**
	 * Save all objects in this cache to an XML document.
	 *
	 * @param	xmlFileName	 Name of XML file to save to.
	 *
	 * @exception	IOException
	 *				Thrown if an IO error occurs.
	 *
	 * @exception	XMLException
	 *				Thrown if an XML error occurs.
	 */
	public synchronized void save(String xmlFilename)
		throws IOException, XMLException
	{
		XMLBeanWriter wtr = new XMLBeanWriter();
		Class<E>[] classes = _cache.getAllClasses();
		for (int i = 0; i < classes.length; ++i)
		{
			for (Iterator<E> it = _cache.getAllForClass(classes[i]);
					it.hasNext();)
			{
				wtr.addToRoot(it.next());
			}
		}
		wtr.save(xmlFilename);
	}

	/**
	 * Save all objects of type <CODE>objClass</CODE> to an XML document.
	 *
	 * @param	xmlFileName	 Name of XML file to save to.
	 * @param	forClass		Class of objects to be saved.
	 *
	 * @exception	IOException
	 *				Thrown if an IO error occurs.
	 *
	 * @exception	XMLException
	 *				Thrown if an XML error occurs.
	 */
	public synchronized void saveAllForClass(String xmlFilename,
	                                         Class<E> forClass)
		throws IOException, XMLException
	{
		XMLBeanWriter wtr = new XMLBeanWriter();
		for (Iterator<E> it = _cache.getAllForClass(forClass); it.hasNext();)
		{
			wtr.addToRoot(it.next());
		}
		wtr.save(xmlFilename);
	}
}
