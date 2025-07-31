package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.FormButtonQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormButtonField extends AmiWebScriptBaseMemberMethods<FormButtonQueryField> {

	private AmiWebScriptMemberMethods_FormButtonField() {
		super();
		addMethod(CLICK);
		registerCallbackDefinition(FormButtonQueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<FormButtonQueryField> CLICK = new AmiAbstractMemberMethod<FormButtonQueryField>(FormButtonQueryField.class, "click",
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, FormButtonQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getForm().onFieldValueChanged(targetObject.getForm().getEditableForm(), targetObject.getField(), null);
			return null;
		}
		@Override
		protected String getHelp() {
			return "Fires an on-user-clicked event.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormButtonField";
	}

	@Override
	public String getVarTypeDescription() {
		return "A button within a FormPanel.";
	}

	@Override
	public Class<FormButtonQueryField> getVarType() {
		return FormButtonQueryField.class;
	}

	@Override
	public Class<FormButtonQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormButtonField INSTANCE = new AmiWebScriptMemberMethods_FormButtonField();
}
