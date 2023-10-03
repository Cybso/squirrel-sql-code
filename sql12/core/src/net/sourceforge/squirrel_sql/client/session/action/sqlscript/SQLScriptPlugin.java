package net.sourceforge.squirrel_sql.client.session.action.sqlscript;

/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.*;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.prefs.SQLScriptPreferencesTab;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.sqltofile.SQLToFileHandler;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.table_script.*;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import javax.swing.*;

/**
 * The SQL Script plugin class.
 */
public class SQLScriptPlugin extends DefaultSessionPlugin
{
	public static final String BUNDLE_BASE_NAME = "net.sourceforge.squirrel_sql.plugins.sqlscript.sqlscript";

	private interface IMenuResourceKeys
	{
		String SCRIPTS = "scripts";
	}

	private IResources _resources;

	private IPluginResourcesFactory _resourcesFactory = new PluginResourcesFactory();
	/**
	 * @param resourcesFactory the resourcesFactory to set
	 */
	public void setResourcesFactory(IPluginResourcesFactory resourcesFactory)
	{
		_resourcesFactory = resourcesFactory;
	}

	/**
	 * Return the internal name of this plugin.
	 * 
	 * @return the internal name of this plugin.
	 */
	public String getInternalName()
	{
		return "sqlscript";
	}

	/**
	 * Return the descriptive name of this plugin.
	 * 
	 * @return the descriptive name of this plugin.
	 */
	public String getDescriptiveName()
	{
		return "SQL Scripts Plugin";
	}

	/**
	 * Returns the current version of this plugin.
	 * 
	 * @return the current version of this plugin.
	 */
	public String getVersion()
	{
		return "1.3";
	}

	/**
	 * Returns the authors name.
	 * 
	 * @return the authors name.
	 */
	public String getAuthor()
	{
		return "Johan Compagner";
	}

	/**
	 * Returns the name of the change log for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the changelog file name or <TT>null</TT> if plugin doesn't have a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Help file name or <TT>null</TT> if plugin doesn't have a help file.
	 */
	public String getHelpFileName()
	{
		return "doc/readme.html";
	}

	/**
	 * Returns the name of the Licence file for the plugin. This should be a text or HTML file residing in the
	 * <TT>getPluginAppSettingsFolder</TT> directory.
	 * 
	 * @return the Licence file name or <TT>null</TT> if plugin doesn't have a licence file.
	 */
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	/**
	 * @return Comma separated list of contributors.
	 */
	public String getContributors()
	{
		return "Gerd Wagner, John Murga, Rob Manning, Stefan Willinger";
	}

	/**
	 * Create preferences panel for the Global Preferences dialog.
	 * 
	 * @return Preferences panel.
	 */
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		SQLScriptPreferencesTab tab = new SQLScriptPreferencesTab();
		return new IGlobalPreferencesPanel[]
			{ tab };
	}

	/**
	 * Initialize this plugin.
	 */
	public synchronized void initialize() throws PluginException
	{
		super.initialize();
		IApplication app = getApplication();

		_resources = _resourcesFactory.createResource(BUNDLE_BASE_NAME, this);

		ActionCollection coll = app.getActionCollection();
		coll.add(new CreateTableScriptAction());
		coll.add(new CreateSelectScriptAction());
		coll.add(new DropTableScriptAction());
		coll.add(new CreateDataScriptAction());
		coll.add(new CreateTemplateDataScriptAction());
		coll.add(new CreateDataScriptOfCurrentSQLAction());
		coll.add(new CreateTableOfCurrentSQLAction());
		coll.add(new CreateInsertStatementsFileOfCurrentSQLAction());
		coll.add(new CreateFileOfCurrentSQLAction());
		coll.add(new CreateInsertStatementsFileOfSelectedTablesSQLAction());
		coll.add(new CreateFileOfSelectedTablesAction());
		createMenu();

		//SQLScriptPreferencesManager.initialize(this);
	}

	/**
	 * Application is shutting down so save data.
	 */
	public void unload()
	{
		super.unload();
		//SQLScriptPreferencesManager.unload();
	}


	/**
	 * Called when a session started. Add commands to popup menu in object tree.
	 * 
	 * @param session
	 *        The session that is starting.
	 * 
	 * @return <TT>true</TT> to indicate that this plugin is applicable to passed session.
	 */
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		addActionsToPopup(session);
		ISQLPanelAPI sqlPaneAPI = session.getSessionPanel().getMainSQLPaneAPI();
		sqlPaneAPI.addSQLExecutionListener(new SQLToFileHandler(session, sqlPaneAPI));

		PluginSessionCallback ret = new PluginSessionCallback()
		{
			public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
			{
				ActionCollection coll = sess.getApplication().getActionCollection();
				sqlInternalFrame.addSeparatorToToolbar();
				sqlInternalFrame.addToToolbar(coll.get(CreateTableOfCurrentSQLAction.class)); 
				sqlInternalFrame.addToToolbar(coll.get(CreateFileOfCurrentSQLAction.class));

				initSqlPanelApi(sqlInternalFrame.getMainSQLPanelAPI());

			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
			{
				initObjectTree(objectTreeInternalFrame.getObjectTreeAPI());
			}

			@Override
			public void objectTreeInSQLTabOpened(ObjectTreePanel objectTreePanel)
			{
				initObjectTree(objectTreePanel);
			}

			@Override
         public void additionalSQLTabOpened(AdditionalSQLTab additionalSQLTab)
         {
				initSqlPanelApi(additionalSQLTab.getSQLPanelAPI());
         }
      };

		return ret;
	}


	private void addActionsToPopup(ISession session)
	{
		ActionCollection coll = getApplication().getActionCollection();
		IObjectTreeAPI objectTreeAPI = FrameWorkAcessor.getObjectTreeAPI(session);

		initObjectTree(objectTreeAPI);

		session.addSeparatorToToolbar();
		session.addToToolbar(coll.get(CreateTableOfCurrentSQLAction.class));
		session.addToToolbar(coll.get(CreateFileOfCurrentSQLAction.class));


		initSqlPanelApi(session.getSessionInternalFrame().getMainSQLPanelAPI());
	}

	private void initObjectTree(IObjectTreeAPI api)
	{
		api.addToPopup(DatabaseObjectType.TABLE, getDBTableMenu(true));
		api.addToPopup(DatabaseObjectType.VIEW, getDBTableMenu(false));
	}

//	private void initObjectTree(IObjectTreeAPI objectTreeAPI)
//	{
//		ISession sess = objectTreeAPI.getSession();
//		ActionCollection coll = sess.getApplication().getActionCollection();
//		objectTreeAPI.addToPopup(DatabaseObjectType.TABLE, coll.get(CreateTableScriptAction.class));
//		objectTreeAPI.addToPopup(DatabaseObjectType.TABLE, coll.get(CreateSelectScriptAction.class));
//		objectTreeAPI.addToPopup(DatabaseObjectType.TABLE, coll.get(DropTableScriptAction.class));
//		objectTreeAPI.addToPopup(DatabaseObjectType.TABLE, coll.get(CreateDataScriptAction.class));
//		objectTreeAPI.addToPopup(DatabaseObjectType.TABLE, coll.get(CreateTemplateDataScriptAction.class));
//	}


	private void initSqlPanelApi(ISQLPanelAPI sqlPanelAPI)
	{
		ActionCollection coll = Main.getApplication().getActionCollection();
		sqlPanelAPI.addToToolsPopUp("sql2table", coll.get(CreateTableOfCurrentSQLAction.class));
		sqlPanelAPI.addToToolsPopUp("sql2ins", coll.get(CreateDataScriptOfCurrentSQLAction.class));
		sqlPanelAPI.addToToolsPopUp("sql2insfile", coll.get(CreateInsertStatementsFileOfCurrentSQLAction.class));
		sqlPanelAPI.addToToolsPopUp("sql2file", coll.get(CreateFileOfCurrentSQLAction.class));

		JMenuItem mnu;
		sqlPanelAPI.addSeparatorToSQLEntryAreaMenu();

		mnu = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CreateTableOfCurrentSQLAction.class));
		_resources.configureMenuItem(coll.get(CreateTableOfCurrentSQLAction.class), mnu);

		mnu = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CreateDataScriptOfCurrentSQLAction.class));
		_resources.configureMenuItem(coll.get(CreateDataScriptOfCurrentSQLAction.class), mnu);

		mnu = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CreateInsertStatementsFileOfCurrentSQLAction.class));
		_resources.configureMenuItem(coll.get(CreateInsertStatementsFileOfCurrentSQLAction.class), mnu);

		mnu = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CreateFileOfCurrentSQLAction.class));
		_resources.configureMenuItem(coll.get(CreateFileOfCurrentSQLAction.class), mnu);

		sqlPanelAPI.addSQLExecutionListener(new SQLToFileHandler(sqlPanelAPI.getSession(), sqlPanelAPI));
	}


	private void createMenu()
	{
		IApplication app = getApplication();
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, getSessionMenu());
	}

	private JMenu getSessionMenu()
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.SCRIPTS);
		_resources.addToMenu(coll.get(CreateDataScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateTemplateDataScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateTableScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateSelectScriptAction.class), menu);
		_resources.addToMenu(coll.get(DropTableScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateDataScriptOfCurrentSQLAction.class), menu);
		_resources.addToMenu(coll.get(CreateInsertStatementsFileOfCurrentSQLAction.class), menu);
		_resources.addToMenu(coll.get(CreateTableOfCurrentSQLAction.class), menu);
		_resources.addToMenu(coll.get(CreateFileOfCurrentSQLAction.class), menu);
		return menu;
	}

	private JMenu getDBTableMenu(boolean includeDrop)
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.SCRIPTS);
		_resources.addToMenu(coll.get(CreateDataScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateTemplateDataScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateTableScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateSelectScriptAction.class), menu);
		_resources.addToMenu(coll.get(CreateFileOfSelectedTablesAction.class), menu);
		_resources.addToMenu(coll.get(CreateInsertStatementsFileOfSelectedTablesSQLAction.class), menu);

		if (includeDrop)
		{
			_resources.addToMenu(coll.get(DropTableScriptAction.class), menu);
		}
		return menu;
	}

	public Object getExternalService()
	{
		//return new SQLScriptExternalService(this);
		return null;
	}

}
