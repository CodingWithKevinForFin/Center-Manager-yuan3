package com.f1.ami.amiscript;

import com.f1.base.Complex;
import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Complex extends AmiScriptBaseMemberMethods<Complex> {

	private AmiScriptMemberMethods_Complex() {
		super();
		addMethod(INIT);
		addMethod(GET_REAL, "real");
		addMethod(GET_IMAGINARY, "imaginary");
		addMethod(GET_ABSOLUTE);
		addMethod(GET_CONJUGATE);
		addMethod(GET_EXPONENTIAL);
		addMethod(GET_LOGARITHM);
		addMethod(GET_SQRT);
		addMethod(GET_SIN);
		addMethod(GET_COS);
		addMethod(GET_SINH);
		addMethod(GET_COSH);
		addMethod(GET_TAN);
		addMethod(GET_NEGATIVE);
	}

	private final static AmiAbstractMemberMethod<Complex> INIT = new AmiAbstractMemberMethod<Complex>(Complex.class, null, Complex.class, Number.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			Number r = (Number) params[0];
			if (r == null)
				return null;
			Number i = (Number) params[1];
			if (i == null)
				return null;
			return new Complex(r.doubleValue(), i.doubleValue());
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "real", "imaginary" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "real", "imaginary" };
		}
		@Override
		protected String getHelp() {
			return "Initialize a Complex number object. Must provide the real and imaginary component.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	private final static AmiAbstractMemberMethod<Complex> GET_REAL = new AmiAbstractMemberMethod<Complex>(Complex.class, "getReal", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.real();
		}
		@Override
		protected String getHelp() {
			return "Returns the real component.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_IMAGINARY = new AmiAbstractMemberMethod<Complex>(Complex.class, "getImaginary", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.imaginary();
		}
		@Override
		protected String getHelp() {
			return "Returns the imaginary component.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_ABSOLUTE = new AmiAbstractMemberMethod<Complex>(Complex.class, "getAbsolute", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.modulus();
		}
		@Override
		protected String getHelp() {
			return "Returns the Absolute value, which is the distance to 0,0 also known as the modulus.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_CONJUGATE = new AmiAbstractMemberMethod<Complex>(Complex.class, "getConjugate", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.conjugate();
		}
		@Override
		protected String getHelp() {
			return "Returns the conjugate value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_EXPONENTIAL = new AmiAbstractMemberMethod<Complex>(Complex.class, "getExponential", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.exponential();
		}
		@Override
		protected String getHelp() {
			return "Returns the exponential value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	private final static AmiAbstractMemberMethod<Complex> GET_LOGARITHM = new AmiAbstractMemberMethod<Complex>(Complex.class, "getLogarithm", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.logarithm();
		}
		@Override
		protected String getHelp() {
			return "Returns the logarithm value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_NEGATIVE = new AmiAbstractMemberMethod<Complex>(Complex.class, "getNegative", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.negative();
		}
		@Override
		protected String getHelp() {
			return "Returns the negative value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_SQRT = new AmiAbstractMemberMethod<Complex>(Complex.class, "getSqrt", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.sqrt();
		}
		@Override
		protected String getHelp() {
			return "Returns the square root.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_SIN = new AmiAbstractMemberMethod<Complex>(Complex.class, "getSin", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.sin();
		}
		@Override
		protected String getHelp() {
			return "Returns the sine.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_COS = new AmiAbstractMemberMethod<Complex>(Complex.class, "getCos", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.cos();
		}
		@Override
		protected String getHelp() {
			return "Returns the cosine.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_SINH = new AmiAbstractMemberMethod<Complex>(Complex.class, "getSinh", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.sinh();
		}
		@Override
		protected String getHelp() {
			return "Returns the hyperbolic sine.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_COSH = new AmiAbstractMemberMethod<Complex>(Complex.class, "getCosh", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.cosh();
		}
		@Override
		protected String getHelp() {
			return "Returns the hyperbolic cosine.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_TAN = new AmiAbstractMemberMethod<Complex>(Complex.class, "getTan", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.tan();
		}
		@Override
		protected String getHelp() {
			return "Returns the tangent.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Complex";
	}
	@Override
	public String getVarTypeDescription() {
		return "A number with a real and imaginary component.";
	}
	@Override
	public Class<Complex> getVarType() {
		return Complex.class;
	}
	@Override
	public Class<Complex> getVarDefaultImpl() {
		return Complex.class;
	}

	public static AmiScriptMemberMethods_Complex INSTANCE = new AmiScriptMemberMethods_Complex();
}
