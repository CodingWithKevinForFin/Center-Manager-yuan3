package com.f1.ami.amiscript;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_StringBuilder extends AmiScriptBaseMemberMethods<StringBuilder> {
	private static final String VAR_TYPE_DESC = "A mutable sequence of characters";
	private static final String VAR_TYPE_NAME = "StringBuilder";

	private AmiScriptMemberMethods_StringBuilder() {
		super();
		addMethod(INIT);
		addMethod(INIT2);
		addMethod(INIT3);
		addMethod(APPEND);
		addMethod(INSERT);
		addMethod(DELETE);
		addMethod(SPLICE);
		addMethod(GET_CAPACITY, "capacity");
		addMethod(GET_LENGTH, "length");
		addMethod(ENSURE_CAPACITY);
		addMethod(TRIM_CAPACITY);
		addMethod(REVERSE);
		addMethod(CLEAR);
		addMethod(SUBSTRING);
		addMethod(TO_STRING);
		addMethod(TO_STRING_AND_CLEAR);
		addMethod(GET_CHAR_AT);
		addMethod(SET_CHAR_AT);
		addMethod(DELETE_CHAR_AT);
	}

	@Override
	public String getVarTypeName() {
		return VAR_TYPE_NAME;
	}

	@Override
	public String getVarTypeDescription() {
		return VAR_TYPE_DESC;
	}

	@Override
	public Class<StringBuilder> getVarType() {
		return StringBuilder.class;
	}

	@Override
	public Class<? extends StringBuilder> getVarDefaultImpl() {
		return StringBuilder.class;
	}

	private static final AmiAbstractMemberMethod<StringBuilder> INIT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, null, StringBuilder.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new StringBuilder();
		}

		@Override
		protected String getHelp() {
			return "Initialize a StringBuilder object";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> INIT2 = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, null, StringBuilder.class,
			CharSequence.class) {

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
			return new String[] { "a String" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new StringBuilder((CharSequence) params[0]);
		}

		@Override
		protected String getHelp() {
			return "Initialize a StringBuilder object with the same characters as the string provided";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> INIT3 = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, null, StringBuilder.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "capacity" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "int capacity" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return new StringBuilder((int) params[0]);
		}

		@Override
		protected String getHelp() {
			return "Initialize a StringBuilder object with an initial capacity";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> APPEND = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "append", StringBuilder.class,
			Object.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value to append" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.append(AmiUtils.s(params[0]));
		}

		@Override
		protected String getHelp() {
			return "Appends the object to the StringBuilder";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> INSERT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "insert", StringBuilder.class,
			Integer.class, Object.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "offset", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "offset to insert at", "value to append" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.insert((Integer) params[0], AmiUtils.s(params[1]));
		}

		@Override
		protected String getHelp() {
			return "Inserts the object to the StringBuilder at an offset";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> DELETE = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "delete", StringBuilder.class,
			Integer.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "start", "end" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "start position inclusive", "end positon exclusive" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.delete((int) params[0], (int) params[1]);
		}

		@Override
		protected String getHelp() {
			return "Deletes the characters in the range from the StringBuidler";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> SPLICE = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "splice", StringBuilder.class,
			Integer.class, Integer.class, Object.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "start", "end", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "start position inclusive", "end positon exclusive", "value to replace with" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.replace((int) params[0], (int) params[1], AmiUtils.s(params[2]));
		}

		@Override
		protected String getHelp() {
			return "Splice and replace the characters in the range from the StringBuidler with the value";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> GET_LENGTH = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "length", Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.length();
		}

		@Override
		protected String getHelp() {
			return "Get the length of the StringBuilder";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> GET_CAPACITY = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "getCapacity", Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.capacity();
		}

		@Override
		protected String getHelp() {
			return "Get the capcity of the StringBuilder";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> ENSURE_CAPACITY = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "ensureCapacity",
			StringBuilder.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "capacity" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "capacity" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.ensureCapacity((int) params[0]);
			return targetObject;
		}

		@Override
		protected String getHelp() {
			return "Ensures the capacity of the Stringbuilder";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> TRIM_CAPACITY = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "trimCapacity",
			StringBuilder.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.trimToSize();
			return targetObject;
		}

		@Override
		protected String getHelp() {
			return "Tries to trim the StringBuilder to its size";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> CLEAR = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "clear", StringBuilder.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return SH.clear(targetObject);
		}

		@Override
		protected String getHelp() {
			return "Clears the StringBuilder";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> REVERSE = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "reverse", StringBuilder.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.reverse();
		}

		@Override
		protected String getHelp() {
			return "Reverses the character sequence in the StringBuilder";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> SUBSTRING = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "substring", String.class,
			Integer.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "start", "end" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "start position inclusive", "end positon exclusive" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.substring((int) params[0], (int) params[1]);
		}

		@Override
		protected String getHelp() {
			return "Returns the substring within the range of StringBuilder";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> TO_STRING = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "toString", String.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.toString();
		}

		@Override
		protected String getHelp() {
			return "Evaluates StringBuilder to a String";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> TO_STRING_AND_CLEAR = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "toStringAndClear",
			String.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return SH.toStringAndClear(targetObject);
		}

		@Override
		protected String getHelp() {
			return "Evaluates StringBuilder to a String and clears it";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> GET_CHAR_AT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "getCharAt", Character.class,
			Integer.class) {

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "offset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "offset" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.charAt((int) params[0]);
		}

		@Override
		protected String getHelp() {
			return "Returns the char at the offset in the Stringbuilder";
		}
	};
	private static final AmiAbstractMemberMethod<StringBuilder> SET_CHAR_AT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "setCharAt", StringBuilder.class,
			Integer.class, Character.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "offset", "char" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "offset", "character" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setCharAt((int) params[0], (char) params[1]);
			return targetObject;
		}

		@Override
		protected String getHelp() {
			return "Set the char at the offset in the Stringbuilder";
		}
	};

	private static final AmiAbstractMemberMethod<StringBuilder> DELETE_CHAR_AT = new AmiAbstractMemberMethod<StringBuilder>(StringBuilder.class, "deleteCharAt",
			StringBuilder.class, Integer.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "offset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "offset" };
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, StringBuilder targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.deleteCharAt((int) params[0]);
		}

		@Override
		protected String getHelp() {
			return "Delete the char at the offset in the Stringbuilder";
		}
	};

	public static final AmiScriptMemberMethods_StringBuilder INSTANCE = new AmiScriptMemberMethods_StringBuilder();
}
