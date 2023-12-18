package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
 * 
 * Based on initial work from Colin Bell
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

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasColorProperties;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This panel allows the user to review and maintain the color properties for an Alias. Background colors can
 * be configured for an Alias, so that each time a session is created, certain component background colors can
 * appear differently from sessions created using other Aliases. This allows the user to get visual clues from
 * SQuirreL about the session that they are interacting with to avoid confusing production database sessions
 * and development ones.
 */
public class ColorPropertiesPanel extends JPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ColorPropertiesPanel.class);

	private JCheckBox toolbarBackgroundColorChk = new JCheckBox("");

	private JButton toolbarBackgroundColorBtn = null;

	private JLabel toolBarBackgroundLbl = null;

	private JPanel toolbarBackgroundColorPnl = new JPanel();

	private Color toolbarBackgroundColor = null;


	private JButton syncComponentBackgroundColorBtn = null;


	// Object Tree

	private JCheckBox objectTreeBackgroundColorChk = new JCheckBox("");

	private JButton objectTreeBackgroundColorBtn = null;

	private JLabel objectTreeBackgroundLbl = null;

	private JPanel objectTreeBackgroundColorPnl = new JPanel();

	private Color objectTreeBackgroundColor = null;

	// Status Bar

	private JCheckBox statusBarBackgroundColorChk = new JCheckBox("");

	private JButton statusBarBackgroundColorBtn = null;

	private JLabel statusBarBackgroundLbl = null;

	private JPanel statusBarBackgroundColorPnl = new JPanel();

	private Color statusBarBackgroundColor = null;


	// Alias
	private JCheckBox aliasBackgroundColorChk = new JCheckBox("");

	private JButton aliasBackgroundColorBtn = null;

	private JLabel aliasBackgroundLbl = null;

	private JPanel aliasBackgroundColorPnl = new JPanel();

	private Color aliasBackgroundColor = null;


	public interface i18n
	{
		// i18n[ColorPropertiesPanel.backgroundColorLabel=Background Color]
		String BACKGROUND_COLOR_LABEL = s_stringMgr.getString("ColorPropertiesPanel.backgroundColorLabel");

		String ALIAS_BACKGROUND_COLOR_CHOOSER_DIALOG_TITLE =
			s_stringMgr.getString("ColorPropertiesPanel.aliasBackgroundColorChooserDialogTitle");

	}

	private SQLAliasColorProperties _props = null;

	public ColorPropertiesPanel(SQLAliasColorProperties props)
	{
		Utilities.checkNull("ColorPropertiesPanel.init", "props", props);

		this._props = props;

		createUserInterface();
	}

	/**
	 * Retrieve the database properties.
	 * 
	 * @return the database properties.
	 */
	public SQLAliasColorProperties getSQLAliasColorProperties()
	{
		if (toolbarBackgroundColorChk.isSelected())
		{
			if (toolbarBackgroundColor != null)
			{
				_props.setOverrideToolbarBackgroundColor(true);
				_props.setToolbarBackgroundColorRgbValue(toolbarBackgroundColor.getRGB());
			}
		}
		else
		{
			_props.setOverrideToolbarBackgroundColor(false);
		}

		if (objectTreeBackgroundColorChk.isSelected())
		{
			if (objectTreeBackgroundColor != null)
			{
				_props.setOverrideObjectTreeBackgroundColor(true);
				_props.setObjectTreeBackgroundColorRgbValue(objectTreeBackgroundColor.getRGB());
			}
		}
		else
		{
			_props.setOverrideObjectTreeBackgroundColor(false);
		}

		if (statusBarBackgroundColorChk.isSelected())
		{
			if (statusBarBackgroundColor != null)
			{
				_props.setOverrideStatusBarBackgroundColor(true);
				_props.setStatusBarBackgroundColorRgbValue(statusBarBackgroundColor.getRGB());
			}
		}
		else
		{
			_props.setOverrideStatusBarBackgroundColor(false);
		}


		if (aliasBackgroundColorChk.isSelected())
		{
			if (aliasBackgroundColor != null)
			{
				_props.setOverrideAliasBackgroundColor(true);
				_props.setAliasBackgroundColorRgbValue(aliasBackgroundColor.getRGB());
			}
		}
		else
		{
			_props.setOverrideAliasBackgroundColor(false);
		}

		return _props;
	}

	private void createUserInterface()
	{
		setLayout(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();

		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;

		// Instructions
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(createInfoPanel(), gbc);

		prepareNewRow(gbc);
		addToolbarColorComponents(gbc);

		prepareNewRow(gbc);
		addSyncComponentColorsWithToolbarCheckBox(gbc);

		gbc.insets.top = 20;
		prepareNewRow(gbc);
		addObjectTreeColorComponents(gbc);
		gbc.insets.top = 5;

		prepareNewRow(gbc);
		addStatusBarColorComponents(gbc);

		prepareNewRow(gbc);
		addAliasColorComponents(gbc);
	}

	private void addSyncComponentColorsWithToolbarCheckBox(final GridBagConstraints gbc)
	{
		++gbc.gridx;
		gbc.gridwidth = 2;
		syncComponentBackgroundColorBtn = new JButton(s_stringMgr.getString("ColorPropertiesPanel.syncComponentBackgroundColorChkLabel"));
		add(syncComponentBackgroundColorBtn, gbc);

		syncComponentBackgroundColorBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				objectTreeBackgroundColorChk.setSelected(true);
				objectTreeBackgroundColor = toolbarBackgroundColor;
				objectTreeBackgroundColorBtn.setEnabled(true);
				objectTreeBackgroundColorPnl.setBackground(toolbarBackgroundColor);
				objectTreeBackgroundColorPnl.setEnabled(true);
				objectTreeBackgroundLbl.setEnabled(true);

				statusBarBackgroundColorChk.setSelected(true);
				statusBarBackgroundColor = toolbarBackgroundColor;
				statusBarBackgroundColorBtn.setEnabled(true);
				statusBarBackgroundColorPnl.setBackground(toolbarBackgroundColor);
				statusBarBackgroundColorPnl.setEnabled(true);
				statusBarBackgroundLbl.setEnabled(true);

				aliasBackgroundColorChk.setSelected(true);
				aliasBackgroundColor = toolbarBackgroundColor;
				aliasBackgroundColorBtn.setEnabled(true);
				aliasBackgroundColorPnl.setBackground(toolbarBackgroundColor);
				aliasBackgroundColorPnl.setEnabled(true);
				aliasBackgroundLbl.setEnabled(true);
			}
		});
	}

	private void addStatusBarColorComponents(GridBagConstraints gbc)
	{
		// Object Tree Color checkbox
		statusBarBackgroundColorChk.setSelected(_props.isOverrideStatusBarBackgroundColor());
		add(statusBarBackgroundColorChk, gbc);

		// Set object tree color button
		++gbc.gridx;
		statusBarBackgroundColorBtn = new JButton(s_stringMgr.getString("ColorPropertiesPanel.statusBarBackgroundColorBtnLabel"));
		add(statusBarBackgroundColorBtn, gbc);

		// Set object tree color panel
		++gbc.gridx;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		statusBarBackgroundColorChk.setSelected(_props.isOverrideStatusBarBackgroundColor());
		statusBarBackgroundColorBtn.setEnabled(_props.isOverrideStatusBarBackgroundColor());

		statusBarBackgroundLbl = new JLabel(i18n.BACKGROUND_COLOR_LABEL);
		statusBarBackgroundLbl.setEnabled(_props.isOverrideStatusBarBackgroundColor());

		statusBarBackgroundColorPnl.add(statusBarBackgroundLbl);
		statusBarBackgroundColorPnl.setEnabled(_props.isOverrideStatusBarBackgroundColor());

		if (_props.isOverrideStatusBarBackgroundColor())
		{
			statusBarBackgroundColor = new Color(_props.getStatusBarBackgroundColorRgbValue());
			statusBarBackgroundColorPnl.setBackground(statusBarBackgroundColor);
		}
		add(statusBarBackgroundColorPnl, gbc);

		statusBarBackgroundColorChk.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (statusBarBackgroundColorChk.isSelected())
				{
					statusBarBackgroundColorBtn.setEnabled(true);
					statusBarBackgroundColorPnl.setEnabled(true);
					statusBarBackgroundLbl.setEnabled(true);
				}
				else
				{
					statusBarBackgroundColorBtn.setEnabled(false);
					statusBarBackgroundColorPnl.setEnabled(false);
					statusBarBackgroundLbl.setEnabled(false);
				}
			}
		});

		statusBarBackgroundColorBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color startColor = statusBarBackgroundColor == null ? Color.WHITE : statusBarBackgroundColor;
				Color newColor =
						JColorChooser.showDialog(GUIUtils.getOwningFrame(ColorPropertiesPanel.this), s_stringMgr.getString("ColorPropertiesPanel.statusBarBackgroundColorChooserDialogTitle"), startColor);

				if (newColor != null)
				{
					statusBarBackgroundColor = newColor;
					statusBarBackgroundColorPnl.setBackground(newColor);
					statusBarBackgroundColorPnl.validate();
				}
			}
		});
	}

	private void addAliasColorComponents(GridBagConstraints gbc)
	{
		// Object Tree Color checkbox
		aliasBackgroundColorChk.setSelected(_props.isOverrideAliasBackgroundColor());
		add(aliasBackgroundColorChk, gbc);

		// Set object tree color button
		++gbc.gridx;
		aliasBackgroundColorBtn = new JButton(s_stringMgr.getString("ColorPropertiesPanel.aliasBackgroundColorBtnLabel"));
		add(aliasBackgroundColorBtn, gbc);

		// Set object tree color panel
		++gbc.gridx;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		aliasBackgroundColorChk.setSelected(_props.isOverrideAliasBackgroundColor());
		aliasBackgroundColorBtn.setEnabled(_props.isOverrideAliasBackgroundColor());

		aliasBackgroundLbl = new JLabel(i18n.BACKGROUND_COLOR_LABEL);
		aliasBackgroundLbl.setEnabled(_props.isOverrideAliasBackgroundColor());

		aliasBackgroundColorPnl.add(aliasBackgroundLbl);
		aliasBackgroundColorPnl.setEnabled(_props.isOverrideAliasBackgroundColor());

		if (_props.isOverrideAliasBackgroundColor())
		{
			aliasBackgroundColor = new Color(_props.getAliasBackgroundColorRgbValue());
			aliasBackgroundColorPnl.setBackground(aliasBackgroundColor);
		}
		add(aliasBackgroundColorPnl, gbc);

		aliasBackgroundColorChk.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (aliasBackgroundColorChk.isSelected())
				{
					aliasBackgroundColorBtn.setEnabled(true);
					aliasBackgroundColorPnl.setEnabled(true);
					aliasBackgroundLbl.setEnabled(true);
				}
				else
				{
					aliasBackgroundColorBtn.setEnabled(false);
					aliasBackgroundColorPnl.setEnabled(false);
					aliasBackgroundLbl.setEnabled(false);
				}
			}
		});

		aliasBackgroundColorBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color startColor = aliasBackgroundColor == null ? Color.WHITE : aliasBackgroundColor;
				Color newColor =
						JColorChooser.showDialog(GUIUtils.getOwningFrame(ColorPropertiesPanel.this), i18n.ALIAS_BACKGROUND_COLOR_CHOOSER_DIALOG_TITLE, startColor);

				if (newColor != null)
				{
					aliasBackgroundColor = newColor;
					aliasBackgroundColorPnl.setBackground(newColor);
					aliasBackgroundColorPnl.validate();
				}
			}
		});

	}


	private void prepareNewRow(final GridBagConstraints gbc)
	{
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 1;
	}

	private void addObjectTreeColorComponents(final GridBagConstraints gbc)
	{
		// Object Tree Color checkbox
		objectTreeBackgroundColorChk.setSelected(_props.isOverrideObjectTreeBackgroundColor());
		add(objectTreeBackgroundColorChk, gbc);

		// Set object tree color button
		++gbc.gridx;
		objectTreeBackgroundColorBtn = new JButton(s_stringMgr.getString("ColorPropertiesPanel.objectTreeBackgroundColorButtonLabel"));
		add(objectTreeBackgroundColorBtn, gbc);

		// Set object tree color panel
		++gbc.gridx;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		objectTreeBackgroundColorChk.setSelected(_props.isOverrideObjectTreeBackgroundColor());
		objectTreeBackgroundColorBtn.setEnabled(_props.isOverrideObjectTreeBackgroundColor());
		objectTreeBackgroundLbl = new JLabel(i18n.BACKGROUND_COLOR_LABEL);
		objectTreeBackgroundColorPnl.add(objectTreeBackgroundLbl);
		objectTreeBackgroundColorPnl.setEnabled(_props.isOverrideObjectTreeBackgroundColor());
		objectTreeBackgroundLbl.setEnabled(_props.isOverrideObjectTreeBackgroundColor());
		if (_props.isOverrideObjectTreeBackgroundColor())
		{
			objectTreeBackgroundColor = new Color(_props.getObjectTreeBackgroundColorRgbValue());
			objectTreeBackgroundColorPnl.setBackground(objectTreeBackgroundColor);
		}
		add(objectTreeBackgroundColorPnl, gbc);

		objectTreeBackgroundColorChk.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (objectTreeBackgroundColorChk.isSelected())
				{
					objectTreeBackgroundColorBtn.setEnabled(true);
					objectTreeBackgroundColorPnl.setEnabled(true);
					objectTreeBackgroundLbl.setEnabled(true);
				}
				else
				{
					objectTreeBackgroundColorBtn.setEnabled(false);
					objectTreeBackgroundColorPnl.setEnabled(false);
					objectTreeBackgroundLbl.setEnabled(false);
				}
			}
		});

		objectTreeBackgroundColorBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color startColor = objectTreeBackgroundColor == null ? Color.WHITE : objectTreeBackgroundColor;
				Color newColor =
					JColorChooser.showDialog(GUIUtils.getOwningFrame(ColorPropertiesPanel.this), s_stringMgr.getString("ColorPropertiesPanel.objectTreeBackgroundColorChooserDialogTitle"),
						startColor);
				if (newColor != null)
				{
					objectTreeBackgroundColor = newColor;
					objectTreeBackgroundColorPnl.setBackground(newColor);
					objectTreeBackgroundColorPnl.validate();
				}
			}
		});

	}

	private void addToolbarColorComponents(final GridBagConstraints gbc)
	{
		// Toolbar Color Checkbox
		toolbarBackgroundColorChk.setSelected(_props.isOverrideToolbarBackgroundColor());
		add(toolbarBackgroundColorChk, gbc);

		// Set toolbar color button
		++gbc.gridx;
		toolbarBackgroundColorBtn = new JButton(s_stringMgr.getString("ColorPropertiesPanel.toolbarBackgroundColorBtnLabel"));
		add(toolbarBackgroundColorBtn, gbc);

		// Set toolbar color panel
		++gbc.gridx;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		toolbarBackgroundColorChk.setSelected(_props.isOverrideToolbarBackgroundColor());
		toolbarBackgroundColorBtn.setEnabled(_props.isOverrideToolbarBackgroundColor());
		toolBarBackgroundLbl = new JLabel(i18n.BACKGROUND_COLOR_LABEL);
		toolbarBackgroundColorPnl.add(toolBarBackgroundLbl);
		toolbarBackgroundColorPnl.setEnabled(_props.isOverrideToolbarBackgroundColor());
		toolBarBackgroundLbl.setEnabled(_props.isOverrideToolbarBackgroundColor());
		if (_props.isOverrideToolbarBackgroundColor())
		{
			toolbarBackgroundColor = new Color(_props.getToolbarBackgroundColorRgbValue());
			toolbarBackgroundColorPnl.setBackground(toolbarBackgroundColor);
		}
		add(toolbarBackgroundColorPnl, gbc);

		toolbarBackgroundColorChk.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (toolbarBackgroundColorChk.isSelected())
				{
					toolbarBackgroundColorBtn.setEnabled(true);
					toolbarBackgroundColorPnl.setEnabled(true);
					toolBarBackgroundLbl.setEnabled(true);
				}
				else
				{
					toolbarBackgroundColorBtn.setEnabled(false);
					toolbarBackgroundColorPnl.setEnabled(false);
					toolBarBackgroundLbl.setEnabled(false);
				}
			}
		});

		toolbarBackgroundColorBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color startColor = toolbarBackgroundColor == null ? Color.WHITE : toolbarBackgroundColor;
				Color newColor =
					JColorChooser.showDialog(GUIUtils.getOwningFrame(ColorPropertiesPanel.this), s_stringMgr.getString("ColorPropertiesPanel.toolbarBackgroundColorChooserDialogTitle"), startColor);
				if (newColor != null)
				{
					toolbarBackgroundColor = newColor;
					toolbarBackgroundColorPnl.setBackground(newColor);
					toolbarBackgroundColorPnl.validate();
				}
			}
		});

	}

	private Box createInfoPanel()
	{
		final Box pnl = new Box(BoxLayout.X_AXIS);
		pnl.add(new MultipleLineLabel(s_stringMgr.getString("ColorPropertiesPanel.instructions")));
		return pnl;
	}

}
