package com.f1.ami.amiscript;

import java.math.BigDecimal;

import com.f1.base.CalcFrame;
import com.f1.utils.casters.Caster_BigDecimal;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_BigDecimal extends AmiScriptBaseMemberMethods<BigDecimal> {

	private AmiScriptMemberMethods_BigDecimal() {
		super();
		addMethod(INIT);
		addMethod(INIT2);
	}

	private final static AmiAbstractMemberMethod<BigDecimal> INIT = new AmiAbstractMemberMethod<BigDecimal>(BigDecimal.class, null, BigDecimal.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BigDecimal target, Object[] params, DerivedCellCalculator caller) {
			Number n = (Number) params[0];
			if (n == null)
				return null;
			return Caster_BigDecimal.INSTANCE.cast(n);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value" };
		}
		@Override
		protected String getHelp() {
			return "Initialize a BigDecimal object";
		}

		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private final static AmiAbstractMemberMethod<BigDecimal> INIT2 = new AmiAbstractMemberMethod<BigDecimal>(BigDecimal.class, null, BigDecimal.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BigDecimal target, Object[] params, DerivedCellCalculator caller) {
			String n = (String) params[0];
			if (n == null)
				return null;
			return Caster_BigDecimal.INSTANCE.cast(n);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value" };
		}
		@Override
		protected String getHelp() {
			return "cast a string to a BigDecimal";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	@Override
	public String getVarTypeName() {
		return "BigDecimal";
	}
	@Override
	public String getVarTypeDescription() {
		return "An unbounded, signed decimal";
	}
	@Override
	public Class<BigDecimal> getVarType() {
		return BigDecimal.class;
	}
	@Override
	public Class<BigDecimal> getVarDefaultImpl() {
		return BigDecimal.class;
	}

	public static AmiScriptMemberMethods_BigDecimal INSTANCE = new AmiScriptMemberMethods_BigDecimal();
}
