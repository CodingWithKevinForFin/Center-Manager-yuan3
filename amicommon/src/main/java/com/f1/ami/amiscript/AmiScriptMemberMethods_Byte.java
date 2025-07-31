package com.f1.ami.amiscript;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Byte extends AmiScriptBaseMemberMethods<Byte> {

	private AmiScriptMemberMethods_Byte() {
		super();
		//		addMethod(this.compareTo);
		//		addMethod(this.equals);
		addMethod(INIT);
		//		addMethod(AmiScriptMemberMethods_Byte.toString);
	}

	private final static AmiAbstractMemberMethod<Byte> INIT = new AmiAbstractMemberMethod<Byte>(Byte.class, null, Object.class, false, Byte.class) {
		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "b" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "a Byte" };
		}

		@Override
		protected String getHelp() {
			return "Initialize a Byte object";
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, Byte targetObject, Object[] params, DerivedCellCalculator caller) {
			return (Byte) params[0];
		}

	};

	@Override
	public String getVarTypeName() {
		return "Byte";
	}

	@Override
	public String getVarTypeDescription() {
		return "A number of type Byte";
	}

	@Override
	public Class<Byte> getVarType() {
		return Byte.class;
	}

	@Override
	public Class<? extends Byte> getVarDefaultImpl() {
		return Byte.class;
	}

	public static AmiScriptMemberMethods_Byte INSTANCE = new AmiScriptMemberMethods_Byte();
}
