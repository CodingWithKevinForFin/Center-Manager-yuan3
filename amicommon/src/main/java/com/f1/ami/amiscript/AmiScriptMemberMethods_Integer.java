package com.f1.ami.amiscript;

import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Integer extends AmiScriptBaseMemberMethods<Integer> {
	public static AmiScriptMemberMethods_Integer INSTANCE;

	private AmiScriptMemberMethods_Integer() {
		super();
		addMethod(INIT);
		addMethod(AmiScriptMemberMethods_Integer.HIGHEST_ONE_BIT);
		addMethod(AmiScriptMemberMethods_Integer.LOWEST_ONE_BIT);
		addMethod(AmiScriptMemberMethods_Integer.NUMBER_OF_LEADING_ZEROES);
		addMethod(AmiScriptMemberMethods_Integer.NUMBER_OF_TRAILING_ZEROS);
	}

	private final static AmiAbstractMemberMethod<Integer> INIT = new AmiAbstractMemberMethod<Integer>(Integer.class, null, Object.class, false, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "i" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "an Integer" };
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, Integer targetObject, Object[] params, DerivedCellCalculator caller) {
			return (Integer) params[0];
		}

		@Override
		protected String getHelp() {
			return "Initialize an Integer object";
		}

	};

	private static final AmiAbstractMemberMethod<Integer> HIGHEST_ONE_BIT = new AmiAbstractMemberMethod<Integer>(Integer.class, "highestOneBit", Integer.class) {
		@Override
		protected String getHelp() {
			return "Returns an int value with at most a single one-bit, in the position of the highest-order (\"leftmost\") one-bit in the specified int value";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Integer targetObject, Object[] params, DerivedCellCalculator caller) {
			return Integer.highestOneBit(targetObject);
		};
	};

	private static final AmiAbstractMemberMethod<Integer> LOWEST_ONE_BIT = new AmiAbstractMemberMethod<Integer>(Integer.class, "lowestOneBit", Integer.class) {
		@Override
		protected String getHelp() {
			return "Returns an int value with at most a single one-bit, in the position of the lowest-order (\"rightmost\") one-bit in the specified int value";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Integer targetObject, Object[] params, DerivedCellCalculator caller) {
			return Integer.lowestOneBit(targetObject);
		};
	};

	private static final AmiAbstractMemberMethod<Integer> NUMBER_OF_LEADING_ZEROES = new AmiAbstractMemberMethod<Integer>(Integer.class, "numberOfLeadingZeros", Integer.class) {
		@Override
		protected String getHelp() {
			return "Returns the number of zero bits preceding the highest-order (\"leftmost\") one-bit in the two's complement binary representation of the specified int value";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Integer targetObject, Object[] params, DerivedCellCalculator caller) {
			return Integer.numberOfLeadingZeros(targetObject);
		};
	};

	private static final AmiAbstractMemberMethod<Integer> NUMBER_OF_TRAILING_ZEROS = new AmiAbstractMemberMethod<Integer>(Integer.class, "numberOfTrailingZeros", Integer.class) {

		@Override
		protected String getHelp() {
			return "Returns the number of zero bits following the lowest-order (\"rightmost\") one-bit in the two's complement binary representation of the specified int value";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Integer targetObject, Object[] params, DerivedCellCalculator caller) {
			return Integer.numberOfTrailingZeros(targetObject);
		};
	};

	@Override
	public String getVarTypeName() {
		return "Integer";
	}

	@Override
	public String getVarTypeDescription() {
		return "An integer number";
	}

	@Override
	public Class<Integer> getVarType() {
		return Integer.class;
	}

	@Override
	public Class<? extends Integer> getVarDefaultImpl() {
		return Integer.class;
	}

	static {
		INSTANCE = new AmiScriptMemberMethods_Integer();
	}
}
