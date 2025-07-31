package com.f1.ami.web.dm.portlets;

import java.util.Map;

import com.f1.ami.portlets.AmiWebHeaderSearchHandler;
import com.f1.ami.web.AmiWebLockedPermissiblePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebVarsManager;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;

public class AmiWebCenterManagerEditTablePortlet extends FormPortlet
		implements FormPortletListener, ConfirmDialogListener, AmiWebLockedPermissiblePortlet, AmiWebHeaderSearchHandler {
	private final FormPortletButton submitButton = new FormPortletButton("Submit");
	private final FormPortletButton cancelButton = new FormPortletButton("Cancel");
	private final PortletManager manager;
	private final AmiWebService service;
	private PortletStyleManager_Form formStyle;
	private final AmiWebVarsManager varsManager;

	//fields
	private FormPortletTextField tableNameField;
	private FormPortletTitleField metadataTitleField;
	private final FormPortletSelectField<String> persistEngineField;
	private FormPortletTextField refreshPeriodMsField;
	private final FormPortletToggleButtonsField<String> broadcastField;

	public AmiWebCenterManagerEditTablePortlet(PortletConfig config) {
		super(config);
		this.manager = getManager();
		this.service = AmiWebUtils.getService(this.manager);
		this.formStyle = getStyleManager();
		this.varsManager = this.service.getVarsManager();
		this.getFormPortletStyle().setCssStyle("_bg=#e2e2e2");

		//table name
		tableNameField = addField(new FormPortletTextField("Table Name: ").setValue("orders").setDisabled(true));
		tableNameField.setBorderWidth(0);

		//metadata
		metadataTitleField = addField(new FormPortletTitleField("Metadata"));

		//persist engine
		this.persistEngineField = addField(new FormPortletSelectField<String>(String.class, "PersistEngine: "));
		this.persistEngineField.addOption(null, "<null>");
		this.persistEngineField.addOption("TEXT", "TEXT");
		this.persistEngineField.addOption("FAST", "FAST");

		//refreshPeriodMs
		this.refreshPeriodMsField = addField(new FormPortletTextField("Table Name: ").setValue("2000").setDisabled(true));

		//broadcast 
		this.broadcastField = addField(new FormPortletToggleButtonsField<String>(String.class, "Broadcast: "));
		this.broadcastField.addOption("Broadcast_true", "true");
		this.broadcastField.addOption("Broadcast_false", "false");
		this.broadcastField.setValue("Broadcast_true");
		this.broadcastField.setDisabled(true);

		//columns config

		addButton(this.submitButton);
		addButton(this.cancelButton);
		addFormPortletListener(this);
	}

	@Override
	public void doSearch() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSearchNext() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSearchPrevious() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

}
