package com.f1.ami.web.centermanager.editor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.web.AmiWebFormatterManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.centermanager.AmiCenterEntityConsts;
import com.f1.ami.web.centermanager.AmiCenterManagerUtils;
import com.f1.ami.web.centermanager.graph.nodes.AmiCenterGraphNode_Table;
import com.f1.ami.web.centermanager.nuweditor.AmiCenterManagerAbstractEditCenterObjectPortlet;
import com.f1.base.Action;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ResultMessage;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTableEditListener;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.WebColumnEditConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.table.impl.WebCellStyleWrapperFormatter;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.string.sqlnode.CreateTableNode;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;

public class AmiCenterManagerEditColumnPortlet extends AmiCenterManagerAbstractEditCenterObjectPortlet
		implements WebContextMenuListener, WebContextMenuFactory, FastTableEditListener {

	final private AmiWebService service;

	private FormPortlet tableInfoPortlet;

	private FormPortletTextField tableNameField;
	private FormPortletSelectField<String> tablePersistEngineField;
	private FormPortletCheckboxField tableBroadCastField;
	private FormPortletTextField tableRefreshPeriodMsField;
	private FormPortletSelectField<String> tableOnUndefColumnField;
	private FormPortletTextField tableInitialCapacityField;

	private FastTablePortlet columnMetadata;
	private boolean enableColumnEditing = false;
	private HasherMap<String, TableEditableColumn> editableColumnIds = new HasherMap<String, TableEditableColumn>();

	private AmiCenterManagerColumnMetaDataEditForm columnMetaDataEditForm;

	private static final String BG_GREY = "_bg=#4c4c4c";

	public AmiCenterManagerEditColumnPortlet(PortletConfig config, boolean isAdd) {
		super(config, isAdd);
		this.service = AmiWebUtils.getService(getManager());
		PortletManager manager = service.getPortletManager();

		tableInfoPortlet = new FormPortlet(manager.generateConfig());
		tableNameField = tableInfoPortlet.addField(new FormPortletTextField("Name"));
		tablePersistEngineField = tableInfoPortlet.addField(new FormPortletSelectField<String>(String.class, "PersistEngine"));
		tablePersistEngineField.addOption(AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_NONE, AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_NONE);
		tablePersistEngineField.addOption(AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_FAST, AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_FAST);
		tablePersistEngineField.addOption(AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_HISTORICAL, AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_HISTORICAL);
		tablePersistEngineField.addOption(AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_TEXT, AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_TEXT);

		tableBroadCastField = tableInfoPortlet.addField(new FormPortletCheckboxField("Broadcast"));
		tableRefreshPeriodMsField = tableInfoPortlet.addField(new FormPortletTextField("RefreshPeriodMs"));

		tableOnUndefColumnField = tableInfoPortlet.addField(new FormPortletSelectField<String>(String.class, "OnUndefColumn"));
		tableOnUndefColumnField.addOption(AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_REJECT, AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_REJECT);
		tableOnUndefColumnField.addOption(AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_IGNORE, AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_IGNORE);
		tableOnUndefColumnField.addOption(AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_ADD, AmiCenterEntityConsts.ON_UNDEF_COLUMN_OPTION_ADD);

		tableInitialCapacityField = tableInfoPortlet.addField(new FormPortletTextField("InitialCapacity"));
		tableInfoPortlet.addFormPortletListener(this);
		//init table
		this.columnMetadata = new FastTablePortlet(generateConfig(), new BasicTable(new Class<?>[] { String.class, String.class, String.class, Boolean.class, Integer.class },
				new String[] { "columnName", "dataType", "options", "noNull", "position" }), "Column Configuration");
		AmiWebFormatterManager fm = service.getFormatterManager();
		this.columnMetadata.getTable().addColumn(true, "Column Name", "columnName", fm.getBasicFormatter()).setWidth(150);
		this.columnMetadata.getTable().addColumn(true, "Data Type", "dataType", fm.getBasicFormatter());
		this.columnMetadata.getTable().addColumn(true, "Options", "options", fm.getBasicFormatter());
		this.columnMetadata.getTable().addColumn(true, "NoNull", "noNull", fm.getBasicFormatter());
		this.columnMetadata.getTable().addColumn(true, "Position", "position", fm.getIntegerWebCellFormatter());
		//note: position column is uneditable
		editableColumnIds.put("columnName", new TableEditableColumn("columnName", WebColumnEditConfig.EDIT_TEXTFIELD));
		editableColumnIds.put("dataType",
				new TableEditableColumn("dataType",
						Arrays.asList(new String[] { AmiConsts.TYPE_NAME_STRING, AmiConsts.TYPE_NAME_LONG, AmiConsts.TYPE_NAME_INTEGER, AmiConsts.TYPE_NAME_BYTE,
								AmiConsts.TYPE_NAME_SHORT, AmiConsts.TYPE_NAME_DOUBLE, AmiConsts.TYPE_NAME_FLOAT, AmiConsts.TYPE_NAME_BOOLEAN, AmiConsts.TYPE_NAME_UTC,
								AmiConsts.TYPE_NAME_UTCN, AmiConsts.TYPE_NAME_BINARY, AmiConsts.TYPE_NAME_ENUM, AmiConsts.TYPE_NAME_CHAR, AmiConsts.TYPE_NAME_BIGINT,
								AmiConsts.TYPE_NAME_BIGDEC, AmiConsts.TYPE_NAME_COMPLEX, AmiConsts.TYPE_NAME_UUID })));
		editableColumnIds.put("options", new TableEditableColumn("options", WebColumnEditConfig.EDIT_TEXTFIELD));
		editableColumnIds.put("noNull", new TableEditableColumn("noNull", WebColumnEditConfig.EDIT_CHECKBOX));
		this.columnMetadata.getTable().sortRows("position", true, true, false);
		this.columnMetadata.setDialogStyle(AmiWebUtils.getService(getManager()).getUserDialogStyleManager());
		this.columnMetadata.addOption(FastTablePortlet.OPTION_TITLE_BAR_COLOR, "#6f6f6f");
		this.columnMetadata.addOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, true);
		//add listener
		this.columnMetadata.getTable().addMenuListener(this);
		//have the ability to create and respond to menu items
		this.columnMetadata.getTable().setMenuFactory(this);

		//enable table editing

		this.columnMetaDataEditForm = new AmiCenterManagerColumnMetaDataEditForm(generateConfig(), null, AmiCenterManagerColumnMetaDataEditForm.MODE_EDIT);

		DividerPortlet div = new DividerPortlet(generateConfig(), true, this.columnMetadata, this.columnMetaDataEditForm);

		this.addChild(tableInfoPortlet, 0, 0, 1, 1);
		//		this.addChild(columnMetadata, 0, 1, 1, 1);
		//		this.addChild(columnMetaDataEditForm, 1, 1, 1, 1);
		this.addChild(div, 0, 1, 1, 2);
		div.setOffsetFromTopPx(500);
		sendAuth();
		if (!isAdd) {
			//initColumnMetadata(tableName);
			this.tableInfoPortlet.addField(enableEditingCheckbox);
			enableEditingCheckbox.setWidth(20).setHeightPx(DEFAULT_ROWHEIGHT).setLeftPosPx(DEFAULT_LEFTPOS + NAME_WIDTH + TYPE_WIDTH + DEFAULT_X_SPACING * 3 + 60)
					.setTopPosPx(DEFAULT_TOPPOS);
		}

	}

	public AmiCenterManagerEditColumnPortlet(PortletConfig config, String tableSql, AmiCenterGraphNode_Table correlationNode) {
		this(config, false);
		this.correlationNode = correlationNode;
		this.importFromText(tableSql, new StringBuilder());
		enableEdit(false);
	}

	public SmartTable getColumnTable() {
		return this.columnMetadata.getTable().getTable();
	}

	private static Map<String, String> parseOptions(String option) {
		HasherMap<String, String> m = new HasherMap<String, String>(CaseInsensitiveHasher.INSTANCE);
		List<String> l = SH.splitToList(" ", option);
		for (String s : l) {
			String key, value = null;
			if (s.contains("=")) {
				key = SH.beforeFirst(s, "=");
				value = SH.afterFirst(s, "=");
			} else {
				key = s;
				value = "true";
			}
			m.put(key, value);
		}
		return m;
	}

	private void sendAuth() {
		AmiCenterQueryDsRequest request = prepareRequest();
		if (request == null)
			return;
		service.sendRequestToBackend(this, request);
	}

	private void prepareRequestToBackend(String query) {
		AmiCenterQueryDsRequest request = prepareRequest();
		request.setQuery(query);
		service.sendRequestToBackend(this, request);
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		if (result.getError() != null) {
			getManager().showAlert("Internal Error:" + result.getError().getMessage(), result.getError());
			return;
		}
		AmiCenterQueryDsResponse response = (AmiCenterQueryDsResponse) result.getAction();
		List<Table> tables = response.getTables();
		if (response.getOk() && tables != null && tables.size() == 1) {
			Table t = tables.get(0);
			for (Row r : t.getRows()) {
				String columnName = (String) r.get("ColumnName");
				String dataType = (String) r.get("DataType");
				String options = (String) r.get("Options");
				Boolean noNull = (Boolean) r.get("NoNull");
				Integer position = (Integer) r.get("Position");
				this.columnMetadata.addRow(columnName, dataType, options, noNull, position);
			}
		}

	}

	public void initColumnMetadata(String t) {
		this.columnMetadata.clearRows();
		prepareRequestToBackend("SHOW COLUMNS WHERE TableName ==" + "\"" + t + "\";");
	}

	private void onUserEditStart() {
		if (!this.columnMetadata.isEditing())
			this.columnMetadata.startEdit(columnMetadata.getTable().getSelectedRows(), this.editableColumnIds, this);
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		//edit logic goes here
		if (this.enableColumnEditing) {
			int selectedCount = this.columnMetadata.getTable().getSelectedRows().size();
			if (selectedCount == 1)
				onUserEditStart();
		}
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		String targetColumnName = null;
		byte actionMode = -1;
		String dialogTitle = null;
		if (SH.startsWith(action, "add_column_")) {
			String temp = SH.afterFirst(action, "add_column_");
			if (temp.startsWith("before")) {
				actionMode = AmiCenterManagerColumnMetaDataEditForm.ACTION_ADD_BEFORE;
				targetColumnName = SH.afterFirst(temp, "before_");
			} else if (temp.startsWith("after")) {
				actionMode = AmiCenterManagerColumnMetaDataEditForm.ACTION_ADD_AFTER;
				targetColumnName = SH.afterFirst(temp, "after_");
			}
			dialogTitle = "Add Column";
		} else if (SH.startsWith(action, "drop_column_")) {
			actionMode = AmiCenterManagerColumnMetaDataEditForm.ACTION_DROP;
			targetColumnName = SH.afterFirst(action, "drop_column_");
			dialogTitle = "Drop Column";
			String query = "ALTER TABLE " + "foo" + " DROP " + targetColumnName;
			getManager().showDialog("Drop Column", new AmiCenterManagerSubmitEditScriptPortlet(this.service, generateConfig(), query),
					AmiCenterManagerSubmitEditScriptPortlet.DEFAULT_PORTLET_WIDTH, AmiCenterManagerSubmitEditScriptPortlet.DEFAULT_PORTLET_HEIGHT);
			return;
		}

		getManager().showDialog(dialogTitle,
				new AmiCenterManagerColumnMetaDataEditForm(generateConfig(), null, AmiCenterManagerColumnMetaDataEditForm.MODE_ADD, targetColumnName, actionMode),
				AmiCenterManagerSubmitEditScriptPortlet.DEFAULT_PORTLET_WIDTH, AmiCenterManagerSubmitEditScriptPortlet.DEFAULT_PORTLET_HEIGHT);

	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
		this.columnMetaDataEditForm.resetForm();
		String dataType = (String) row.get("dataType");
		String columnName = (String) row.get("columnName");
		Boolean noNull = (Boolean) row.get("noNull");
		Integer position = (Integer) row.get("position");
		String options = (String) row.get("options");
		FormPortletTextField f1 = (FormPortletTextField) this.columnMetaDataEditForm.getForm().getFieldByName("columnName");
		f1.setValue(columnName);
		f1.setDisabled(false);
		FormPortletSelectField f2 = (FormPortletSelectField) this.columnMetaDataEditForm.getForm().getFieldByName(AmiCenterManagerColumnMetaDataEditForm.VARNAME_COLUMN_DATA_TYPE);
		f2.setValue(AmiUtils.parseTypeName(dataType));
		f2.setDisabled(false);
		FormPortletCheckboxField f3 = (FormPortletCheckboxField) this.columnMetaDataEditForm.getForm().getFieldByName(AmiConsts.NONULL);
		f3.setValue(noNull);
		f3.setDisabled(false);
		Map<String, String> m = parseOptions(options);

		//set the column cache
		this.columnMetaDataEditForm.setColumnCache(AmiCenterManagerColumnMetaDataEditForm.VARNAME_COLUMN_DATA_TYPE, dataType);
		this.columnMetaDataEditForm.setColumnCache(AmiCenterManagerColumnMetaDataEditForm.VARNAME_COLUMN_NAME, columnName);
		this.columnMetaDataEditForm.setColumnCache(AmiCenterManagerColumnMetaDataEditForm.VARNAME_NONULL, Caster_String.INSTANCE.cast(noNull));
		for (Entry<String, String> e : m.entrySet()) {
			this.columnMetaDataEditForm.setColumnCache(e.getKey(), e.getValue());
		}

		switch (AmiUtils.parseTypeName(dataType)) {
			case AmiDatasourceColumn.TYPE_BIGDEC:
			case AmiDatasourceColumn.TYPE_BIGINT:
			case AmiDatasourceColumn.TYPE_BOOLEAN:
			case AmiDatasourceColumn.TYPE_BYTE:
			case AmiDatasourceColumn.TYPE_CHAR:
			case AmiDatasourceColumn.TYPE_COMPLEX:
			case AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_FLOAT:
			case AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_SHORT:
			case AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_UTCN:
			case AmiDatasourceColumn.TYPE_UUID:
				setNoBroadCast(m);
				//enable common options
				this.columnMetaDataEditForm.disableCommonOptions(false);
				break;
			case AmiDatasourceColumn.TYPE_STRING:
				setNoBroadCast(m);
				//enable common options
				this.columnMetaDataEditForm.disableCommonOptions(false);

				Boolean isCompact = Caster_Boolean.INSTANCE.cast(m.get(AmiConsts.COMPACT));
				Boolean isAscii = Caster_Boolean.INSTANCE.cast(m.get(AmiConsts.ASCII));
				Boolean isBitmap = Caster_Boolean.INSTANCE.cast(m.get(AmiConsts.BITMAP));
				Boolean isOndisk = Caster_Boolean.INSTANCE.cast(m.get(AmiConsts.ONDISK));
				Boolean isEnum = Caster_Boolean.INSTANCE.cast(m.get(AmiConsts.TYPE_NAME_ENUM));
				boolean isCache = m.get(AmiConsts.CACHE) != null;
				if (isCache) {
					getColumnOptionEditField(AmiConsts.CACHE).setValue(true).setDisabled(false);
					String rawCacheValue = (String) m.get(AmiConsts.CACHE);
					int cacheValue = parseCacheValue(rawCacheValue).getA();
					String cacheUnit = parseCacheValue(rawCacheValue).getB();
					if (SH.isnt(cacheUnit))
						cacheUnit = AmiConsts.CACHE_UNIT_DEFAULT_BYTE;
					byte cacheUnitByte = AmiCenterManagerUtils.toCacheUnitCode(cacheUnit);
					this.columnMetaDataEditForm.getCacheValueField().setValue(SH.toString(cacheValue));
					this.columnMetaDataEditForm.getCacheUnitField().setValue(cacheUnitByte);
				}
				//enable edit for all string options
				this.columnMetaDataEditForm.disableStringOptions(false);
				if (isCompact != null && Boolean.TRUE.equals(isCompact)) {
					getColumnOptionEditField(AmiConsts.COMPACT).setValue(true).setDisabled(false);
					getColumnOptionEditField(AmiConsts.BITMAP).setDisabled(true);
				}
				if (isAscii != null && Boolean.TRUE.equals(isAscii)) {
					getColumnOptionEditField(AmiConsts.ASCII).setValue(true).setDisabled(false);
				}
				if (isBitmap != null && Boolean.TRUE.equals(isBitmap)) {
					getColumnOptionEditField(AmiConsts.BITMAP).setValue(true).setDisabled(false);
					getColumnOptionEditField(AmiConsts.COMPACT).setDisabled(true);

				}
				if (isOndisk != null && Boolean.TRUE.equals(isOndisk)) {
					getColumnOptionEditField(AmiConsts.ONDISK).setValue(true).setDisabled(false);
				}
				if (isEnum != null && Boolean.TRUE.equals(isEnum)) {
					getColumnOptionEditField(AmiConsts.TYPE_NAME_ENUM).setValue(true).setDisabled(false);
				}

				break;
			case AmiDatasourceColumn.TYPE_BINARY:
				setNoBroadCast(m);
				//enable common options
				this.columnMetaDataEditForm.disableCommonOptions(false);
				break;
			default:
				throw new NullPointerException();
		}

	}

	public static Tuple2<Integer, String> parseCacheValue(String s) {
		Tuple2<Integer, String> cacheValue = new Tuple2<Integer, String>();
		StringBuilder digitBuilder = new StringBuilder();
		StringBuilder unitBuilder = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c == '"')
				continue;
			if (Character.isDigit(c))
				digitBuilder.append(c);
			else
				unitBuilder.append(c);
		}
		cacheValue.setA(Integer.parseInt(digitBuilder.toString()));
		cacheValue.setB(unitBuilder.toString());
		return cacheValue;
	}

	private void setNoBroadCast(Map<String, String> m) {
		FormPortletCheckboxField noBroadcastEditField = (FormPortletCheckboxField) this.columnMetaDataEditForm.getForm().getFieldByName("NoBroadcast");
		Boolean noBroadcast = Caster_Boolean.INSTANCE.cast(m.get(AmiConsts.NOBROADCAST));
		noBroadcastEditField.setValue(noBroadcast);
		noBroadcastEditField.setDisabled(false);
	}

	private FormPortletCheckboxField getColumnOptionEditField(String name) {
		return (FormPortletCheckboxField) this.columnMetaDataEditForm.getForm().getFieldByName(name);
	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		FastWebTable ftw = (FastWebTable) table;
		int origRowPos = ftw.getActiveRow().getLocation();
		String origColumnName = (String) ftw.getActiveRow().get("columnName");
		BasicWebMenu m = new BasicWebMenu();
		m.add(new BasicWebMenuLink("Add Column Before " + origColumnName, true, "add_column_before_" + origColumnName));
		m.add(new BasicWebMenuLink("Add Column After " + origColumnName, true, "add_column_after_" + origColumnName));
		m.add(new BasicWebMenuLink("Drop Column", true, "drop_column_" + origColumnName));

		return m;
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		// TODO Auto-generated method stub

	}

	@Override
	public String prepareUseClause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String preparePreUseClause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String exportToText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void importFromText(String text, StringBuilder sink) {
		CreateTableNode cn = AmiCenterManagerUtils.scriptToCreateTableNode(text);
		Map<String, String> tableConfig = AmiCenterManagerUtils.parseAdminNode_Table(cn);
		String tableName = tableConfig.get("name");
		initColumnMetadata(tableName);
		for (Entry<String, String> e : tableConfig.entrySet()) {
			String key = e.getKey();
			String value = e.getValue();
			if ("PersistEngine".equals(key)) {
				String valueToSet = value == null ? AmiCenterEntityConsts.PERSIST_ENGINE_TYPE_NONE : value;
				tablePersistEngineField.setValue(valueToSet);
				tablePersistEngineField.setDefaultValue(valueToSet);
			} else if ("BroadCast".equals(key)) {
				Boolean boolVal = Caster_Boolean.INSTANCE.cast(value);
				boolVal = boolVal == null ? true : boolVal;//dflt is true
				tableBroadCastField.setValue(boolVal);
				tableBroadCastField.setDefaultValue(boolVal);
			} else if ("RefreshPeriodMs".equals(key)) {
				tableRefreshPeriodMsField.setValue(value);
				tableRefreshPeriodMsField.setDefaultValue(value);
			} else if ("OnUndefColumn".equals(key)) {
				tableOnUndefColumnField.setValue(value);
				tableOnUndefColumnField.setDefaultValue(value);
			} else if ("InitialCapacity".equals(key)) {
				tableInitialCapacityField.setValue(value);
				tableInitialCapacityField.setDefaultValue(value);
			}
		}

	}

	public void enableColumnEditing(boolean enable) {
		this.enableColumnEditing = enable;
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		super.onFieldValueChanged(portlet, field, attributes);
	}

	@Override
	public void enableEdit(boolean enable) {
		for (FormPortletField<?> fpf : this.tableInfoPortlet.getFormFields()) {
			if (fpf != this.enableEditingCheckbox)
				fpf.setDisabled(!enable);
		}
		enableColumnEditing(enable);

	}

	@Override
	public void onTableEditComplete(Table origTable, Table editedTable, FastTablePortlet fastTablePortlet, StringBuilder errorSink) {
		if (errorSink.length() > 0) {
			this.columnMetadata.finishEdit();
			getManager().showAlert(errorSink.toString());
			return;
		}
		if (editedTable.getSize() == 0) {
			this.columnMetadata.finishEdit();
			return;
		}
		this.columnMetadata.finishEdit();
	}

	@Override
	public void onTableEditAbort(FastTablePortlet fastTablePortlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEditCell(int x, int y, String v) {
		final WebColumn pos = this.columnMetadata.getTable().getVisibleColumn(x);
		final String[] cols = pos.getTableColumns();
		final Column col = this.columnMetadata.getTable().getTable().getColumn(cols[0]);
		WebCellFormatter f = pos.getCellFormatter();
		if (f instanceof WebCellStyleWrapperFormatter)
			f = ((WebCellStyleWrapperFormatter) f).getInner();
		final Object v2;
		if (f instanceof NumberWebCellFormatter) {
			NumberWebCellFormatter f2 = (NumberWebCellFormatter) f;
			try {
				v2 = SH.isEmpty(v) ? null : f2.getFormatter().parse(v);
			} catch (Exception e) {
				return;
			}
		} else
			v2 = v;
		final Object cast = col.getTypeCaster().cast(v2, false, false);
		if (y < this.columnMetadata.getTable().getRowsCount())
			this.columnMetadata.getTable().getRow(y).putAt(col.getLocation(), cast);
	}

	@Override
	public Object getEditOptions(WebColumnEditConfig cfg, Row row) {
		//convert List<String> to comma-delimetered strings
		List<String> listOptions = cfg.getEditSelectOptions();
		String strOptions = SH.join(',', listOptions);
		return strOptions;
	}

	public static class TableEditableColumn implements WebColumnEditConfig {
		private String columnId;
		private String editOptionFormula;
		private List<String> editSelectOptions;
		private byte editType;//checkbox, textfield, select

		public TableEditableColumn(String columnId, byte editType) {
			this.columnId = columnId;
			this.editType = editType;
		}

		public TableEditableColumn(String columnId, List<String> editSelectOptions) {
			this(columnId, WebColumnEditConfig.EDIT_SELECT);
			this.editSelectOptions = editSelectOptions;
		}

		@Override
		public String getEditId() {
			return getColumnId();
		}

		@Override
		public String getColumnId() {
			return columnId;
		}

		@Override
		public String getEditOptionFormula() {
			return editOptionFormula;
		}

		@Override
		public List<String> getEditSelectOptions() {
			return editSelectOptions;
		}

		@Override
		public byte getEditType() {
			return editType;
		}

		@Override
		public String getEnableLastNDays() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean getDisableFutureDays() {
			throw new UnsupportedOperationException();

		}

	}

}
