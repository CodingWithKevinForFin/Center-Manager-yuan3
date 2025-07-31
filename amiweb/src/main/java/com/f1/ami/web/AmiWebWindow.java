package com.f1.ami.web;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;

public class AmiWebWindow extends Window {

	public AmiWebWindow(DesktopPortlet owner, String name, Portlet portlet, int zindex, int flags) {
		super(owner, name, portlet, zindex, flags);
	}

}
