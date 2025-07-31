package com.f1.ami.amiscript;

import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Object extends AmiScriptBaseMemberMethods<Object> {

	public static final ObjectToJsonConverter JSON_CONVERTER;
	static {
		JSON_CONVERTER = new ObjectToJsonConverter();
		JSON_CONVERTER.setCompactMode(ObjectToJsonConverter.MODE_COMPACT);
		JSON_CONVERTER.registerConverterLowPriority(new AmiJsonConverter());
	}

	//	private AmiService service;

	private AmiScriptMemberMethods_Object() {
		super();
		//		this.service = service;

		addMethod(GET_CLASS_NAME);
		addMethod(TO_JSON);
	}

	private static final AmiAbstractMemberMethod<Object> TO_JSON = new AmiAbstractMemberMethod<Object>(Object.class, "toJson", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Object targetObject, Object[] params, DerivedCellCalculator caller) {
			return JSON_CONVERTER.objectToString(targetObject);
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Returns a json representation of this object.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<Object> GET_CLASS_NAME = new AmiAbstractMemberMethod<Object>(Object.class, "getClassName", String.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Object targetObject, Object[] params, DerivedCellCalculator caller) {
			return sf.getFactory().forType(targetObject.getClass());
		}
		protected String[] buildParamNames() {
			return new String[] {};
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}

		@Override
		protected String getHelp() {
			return "Returns the string name of this object's class type.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Object";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<Object> getVarType() {
		return Object.class;
	}
	@Override
	public Class<Object> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Object INSTANCE = new AmiScriptMemberMethods_Object();
}
