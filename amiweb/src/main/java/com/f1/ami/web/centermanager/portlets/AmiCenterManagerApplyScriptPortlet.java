package com.f1.ami.web.centermanager.portlets;

import java.util.Map;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;

public class AmiCenterManagerApplyScriptPortlet extends GridPortlet implements FormPortletListener {
	final private AmiWebHeaderPortlet header;
	final private AmiCenterManagerReviewApplyScriptPortlet owner;
	final private FormPortlet infoForm;
	final private FormPortlet buttonsFp;
	final private FormPortletButton finishButton;

	public AmiCenterManagerApplyScriptPortlet(PortletConfig config, AmiCenterManagerReviewApplyScriptPortlet parent) {
		super(config);
		owner = parent;
		header = new AmiWebHeaderPortlet(generateConfig());
		header.setInformationHeaderHeight(70);
		header.updateBlurbPortletLayout("Applying SQL script to AMIDB", null);
		header.setShowSearch(false);
		header.setShowBar(false);
		infoForm = new FormPortlet(generateConfig());
		buttonsFp = new FormPortlet(generateConfig());
		buttonsFp.getFormPortletStyle().setLabelsWidth(200);
		buttonsFp.addFormPortletListener(this);
		finishButton = buttonsFp.addButton(new FormPortletButton("Finish"));
		addChild(header, 0, 0);
		addChild(infoForm, 0, 1);
		addChild(buttonsFp, 0, 2);
		setRowSize(2, 50);

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.finishButton) {
			owner.close();
			return;
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

}
