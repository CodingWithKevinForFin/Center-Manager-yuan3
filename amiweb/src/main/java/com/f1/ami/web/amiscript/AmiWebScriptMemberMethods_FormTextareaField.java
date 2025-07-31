package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.TextAreaQueryField;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormTextareaField extends AmiWebScriptBaseMemberMethods<TextAreaQueryField> {

	private AmiWebScriptMemberMethods_FormTextareaField() {
		super();
		addMethod(GET_VALUE, "value");
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	public static final AmiAbstractMemberMethod<TextAreaQueryField> GET_VALUE = new AmiAbstractMemberMethod<TextAreaQueryField>(TextAreaQueryField.class, "getValue", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TextAreaQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns this field's value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormTextareaField";
	}

	@Override
	public String getVarTypeDescription() {
		return "AMI Script Class to represent Text Area Field";
	}

	@Override
	public Class<TextAreaQueryField> getVarType() {
		return TextAreaQueryField.class;
	}

	@Override
	public Class<? extends TextAreaQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormTextareaField INSTANCE = new AmiWebScriptMemberMethods_FormTextareaField();
}
