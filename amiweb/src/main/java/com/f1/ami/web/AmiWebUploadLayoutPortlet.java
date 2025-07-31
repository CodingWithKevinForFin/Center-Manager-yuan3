package com.f1.ami.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField.FileData;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple2;

public class AmiWebUploadLayoutPortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener {
	private static final String CONTENT_TYPE_ZIP = "application/x-zip-compressed";
	private static final String CONTENT_TYPE_AMI = "application/octet-stream";

	private static final Logger log = LH.get();
	private final AmiWebService service;
	private final AmiWebLayoutManager layoutManager;
	private final FormPortlet form;
	private final FormPortletFileUploadField fileUploadField;
	private byte[] fileData;
	private final FormPortletButton uploadLayoutButton;
	private final FormPortletButton cancelUploadLayoutButton;
	private PortletStyleManager_Form formStyle;

	public AmiWebUploadLayoutPortlet(PortletConfig config) {
		super(config);
		this.service = (AmiWebService) getManager().getService(AmiWebService.ID);
		this.formStyle = getManager().getStyleManager().getFormStyle();
		this.layoutManager = this.service.getLayoutManager();
		this.form = new FormPortlet(generateConfig());
		this.fileUploadField = new FormPortletFileUploadField("Browse File: ");
		this.uploadLayoutButton = new FormPortletButton("Upload").setId("upload_layout");
		this.cancelUploadLayoutButton = new FormPortletButton("Cancel").setId("cancel_upload");
		this.form.addField(this.fileUploadField);
		this.form.addButton(uploadLayoutButton);
		this.form.addButton(cancelUploadLayoutButton);
		addStylesToFields();
		addChild(this.form, 0, 0);
		this.form.addFormPortletListener(this);
	}
	public void addStylesToFields() {
		this.fileUploadField.setCssStyle("style.paddingLeft=5px|style.paddingTop=5px");
		if (formStyle.isUseDefaultStyling())
			return;
		this.fileUploadField.setBgColor(formStyle.getDefaultFormBgColor());
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (OH.eq(button.getId(), "upload_layout")) {
			//TODO: temporarily commented out. will be used later.	
			String layoutContentText = getUploadedFileDataAsText();
			if (OH.ne(layoutContentText, null)) {
				if (AmiWebLayoutHelper.isParsingSafeSuccessFul(layoutContentText, getManager())) {
					loadLayout(layoutContentText);
					this.close();
				} else {
					getManager().showAlert("Error uploading layout. Please make sure it is in correct format");
					LH.warning(log, "Error parsing file to JSON");
				}
			}
		} else if (OH.eq(button.getId(), "cancel_upload")) {
			this.close();
		}
	}
	private void upload() {
		FileData fdata = this.fileUploadField.getValue();
		if (fdata.getContentType().equals(CONTENT_TYPE_ZIP)) {
			byte[] zip = fdata.getData();
			Map<String, Tuple2<ZipEntry, byte[]>> entries;
			try {
				entries = IOH.unzip(zip);
				for (String layoutName : entries.keySet()) {
					Tuple2<ZipEntry, byte[]> tuple = entries.get(layoutName);
					String configText = new String(tuple.getB());
					Map<String, Object> config = AmiWebLayoutHelper.parseJsonSafe(configText, getManager());
					// only upload the root file for now.
					ConfirmDialog cdp = this.service.getLayoutFilesManager().loadLayoutDialog(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, configText, null);
					if (cdp != null)
						cdp.addDialogListener(this);
					else
						close();
				}
			} catch (IOException e) {
				LH.warning(log, "Error uploading layout: ", e);
			}
		} else if (fdata.getContentType().equals(CONTENT_TYPE_AMI)) {
			String configText = new String(fdata.getData());
			AmiWebLayoutHelper.parseJsonSafe(configText, getManager());
			ConfirmDialog cdp = this.service.getLayoutFilesManager().loadLayoutDialog(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, configText, null);
			if (cdp != null)
				cdp.addDialogListener(this);
			else
				close();
		}
	}

	boolean isRootFile(Map<String, Object> config) {
		List<String> layouts = (List<String>) config.get("includeFiles");
		return layouts.size() != 0;
	}
	private String getUploadedFileDataAsText() {
		FileData fdata = this.fileUploadField.getValue();
		if (fdata == null) {
			getManager().showAlert("Please select a file for upload");
			return null;
		}
		String contentType = fdata.getContentType();
		if (OH.eq(contentType, "application/octet-stream")) {
			try {
				byte[] bytes = fdata.getData();
				String textData = new String(bytes);
				return textData;
			} catch (Exception e) {
				getManager().showAlert("Error reading file. Please make sure it is in correct format");
				LH.warning(log, "Error reading the file " + getLayoutName() + ": ", e);
			}
		} else {
			getManager().showAlert("Layout format is incompatible");
			LH.warning(log, "Incompatible content type: found " + contentType + " expected: application/octet-stream");
		}
		return null;
	}
	private void loadLayout(String layoutContentText) {
		Map<String, Object> configuration = AmiWebLayoutHelper.parseJsonSafe(layoutContentText, getManager());
		if (configuration.containsKey("metadata") || configuration.containsKey("layouts")) {
			String layoutContentStr = AmiWebLayoutHelper.toJson(configuration, ObjectToJsonConverter.MODE_SEMI);
			ConfirmDialog cdp = service.getLayoutFilesManager().loadLayoutDialog(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, layoutContentStr, null);
			if (cdp != null)
				cdp.addDialogListener(this);
			else
				close();
		} else {
			AmiWebLayoutHelper.importWindowConfig(service, "Imported", configuration, false); // importing window(s) renders non-null return value.
			close();
		}
	}
	public AmiWebLayoutManager getLayoutManager() {
		return this.layoutManager;
	}
	public String getLayoutName() {
		return SH.trim(this.fileUploadField.getValue().getName());
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialogPortlet.ID_YES.equals(id))
			close();
		return true;
	}
}
