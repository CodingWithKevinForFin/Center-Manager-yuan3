package com.f1.ami.web;

import java.util.Set;

import com.f1.ami.web.dm.AmiWebDm;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public abstract class AmiWebPluginPortlet extends AmiWebAbstractDmPortlet {

	private AmiWebPanelPlugin plugin;

	public AmiWebPluginPortlet(PortletConfig config, AmiWebPanelPlugin plugin) {
		super(config);
		this.plugin = plugin;
	}

	@Override
	public void clearAmiData() {
	}

	@Override
	public String getPanelType() {
		return "Plugin";
	}

	@Override
	public com.f1.base.CalcTypes getUsedVariables() {
		return EmptyCalcTypes.INSTANCE;
	}

	@Override
	public void clearUserSelection() {
	}

	@Override
	public String getConfigMenuTitle() {
		return plugin.getDisplayName();
	}
	@Override
	public void getUsedColors(Set<String> sink) {
	}

	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return getVisible();
	}

	@Override
	public boolean isRealtime() {
		return false;
	}

	@Override
	public String getStyleType() {
		return this.plugin.getStyleType().getName();
	}
}
