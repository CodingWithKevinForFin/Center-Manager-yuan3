package com.f1.ami.web.centermanager.portlets;

import java.util.Map;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.AmiWebFormPortletAmiScriptField;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;

public class AmiCenterManagerReviewScriptPortlet extends GridPortlet implements FormPortletListener {
	final private AmiWebHeaderPortlet header;
	final private FormPortlet scriptForm;
	final private AmiWebFormPortletAmiScriptField scriptField;
	final private FormPortlet buttonsFp;
	final private FormPortletButton applyButton;
	final private FormPortletButton cancelButton;

	public AmiCenterManagerReviewScriptPortlet(PortletConfig config) {
		super(config);
		header = new AmiWebHeaderPortlet(generateConfig());
		header.setInformationHeaderHeight(70);
		header.updateBlurbPortletLayout("Review the SQL Script to be Applied on AMIDB", null);
		header.setShowSearch(false);
		header.setShowBar(false);
		scriptForm = new FormPortlet(generateConfig());
		scriptField = scriptForm.addField(new AmiWebFormPortletAmiScriptField("", getManager(), AmiWebFormPortletAmiScriptField.LANGUAGE_SCOPE_CENTER_SCRIPT));
		scriptField.setLeftPosPx(0).setTopPosPx(20).setHeightPx(500).setWidthPx(770);
		buttonsFp = new FormPortlet(generateConfig());
		buttonsFp.getFormPortletStyle().setLabelsWidth(200);
		buttonsFp.addFormPortletListener(this);
		applyButton = buttonsFp.addButton(new FormPortletButton("Apply"));
		cancelButton = buttonsFp.addButton(new FormPortletButton("Cancel"));
		addChild(header, 0, 0);
		addChild(scriptForm, 0, 1);
		addChild(buttonsFp, 0, 2);
		setRowSize(2, 50);

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton) {
			close();
			return;
		} else if (button == this.applyButton) {
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
