package com.f1.ami.amiscript;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Short extends AmiScriptBaseMemberMethods<Short> {

	private AmiScriptMemberMethods_Short() {
		super();
		addMethod(BYTE_VALUE);
		//		addMethod(this.compareTo);
		//		addMethod(this.equals);
		addMethod(INIT);
		//		addMethod(AmiScriptMemberMethods_Short.toString);
	}

	private static final AmiAbstractMemberMethod<Short> INIT = new AmiAbstractMemberMethod<Short>(Short.class, null, Object.class, false, Short.class) {
		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "s" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "a Short" };
		}

		@Override
		protected String getHelp() {
			return "Initialize a Short object.";
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, Short targetObject, Object[] params, DerivedCellCalculator caller) {
			return (Short) params[0];
		}

	};

	private static final AmiAbstractMemberMethod<Short> BYTE_VALUE = new AmiAbstractMemberMethod<Short>(Short.class, "byteValue", Byte.class) {
		@Override
		protected String getHelp() {
			return "Returns the value of this Short as a byte after a narrowing primitive conversion.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Short targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.byteValue();
		};
	};

	@Override
	public String getVarTypeName() {
		return "Short";
	}

	@Override
	public String getVarTypeDescription() {
		return "A number of type Short.";
	}

	@Override
	public Class<Short> getVarType() {
		return Short.class;
	}

	@Override
	public Class<? extends Short> getVarDefaultImpl() {
		return Short.class;
	}

	public static AmiScriptMemberMethods_Short INSTANCE = new AmiScriptMemberMethods_Short();
}
