package com.f1.ami.amiscript;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.f1.base.Bytes;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Binary extends AmiScriptBaseMemberMethods<Bytes> {

	private AmiScriptMemberMethods_Binary() {
		super();
		addMethod(INIT);
		addMethod(INIT2);
		addMethod(GET_LENGTH);
		addMethod(TO_BASE64);
		addMethod(TO_TEXT);
		addMethod(TO_TEXT2);
		addCustomDebugProperty("size", Integer.class);
		addCustomDebugProperty("bytes", List.class);
	}

	private final static AmiAbstractMemberMethod<Bytes> INIT = new AmiAbstractMemberMethod<Bytes>(Bytes.class, null, Bytes.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Bytes targetObject, Object[] params, DerivedCellCalculator caller) {
			String s = (String) params[0];
			return new Bytes(s == null ? OH.EMPTY_BYTE_ARRAY : s.getBytes());
		}
		protected String[] buildParamNames() {
			return new String[] { "text" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "text" };
		}

		@Override
		protected String getHelp() {
			return "Create a UTF binary representation of a string, if string is null creates an empty Binary";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private final static AmiAbstractMemberMethod<Bytes> INIT2 = new AmiAbstractMemberMethod<Bytes>(Bytes.class, null, Bytes.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Bytes targetObject, Object[] params, DerivedCellCalculator caller) {
			String s = (String) params[0];
			String charsetName = (String) params[1];
			return new Bytes(s == null ? OH.EMPTY_BYTE_ARRAY : s.getBytes(Charset.forName(charsetName)));
		}
		protected String[] buildParamNames() {
			return new String[] { "text", "charset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "text", "charset" };
		}

		@Override
		protected String getHelp() {
			return "Create a UTF binary representation of a string of a given charset, if string is null creates an empty Binary";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private final static AmiAbstractMemberMethod<Bytes> TO_BASE64 = new AmiAbstractMemberMethod<Bytes>(Bytes.class, "toBase64", String.class) {
		protected StringBuilder sb = new StringBuilder();

		@Override
		public String invokeMethod2(CalcFrameStack sf, Bytes targetObject, Object[] params, DerivedCellCalculator caller) {
			SH.clear(this.sb);
			EncoderUtils.encode64(targetObject.getBytes(), this.sb);
			return SH.toStringAndClear(this.sb);
		}

		@Override
		protected String getHelp() {
			return "Returns the base64 encoding of this Binary object";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private final static AmiAbstractMemberMethod<Bytes> TO_TEXT = new AmiAbstractMemberMethod<Bytes>(Bytes.class, "toText", String.class) {
		@Override
		public String invokeMethod2(CalcFrameStack sf, Bytes targetObject, Object[] params, DerivedCellCalculator caller) {
			return new String(targetObject.getBytes());
		}

		@Override
		protected String getHelp() {
			return "Tries to return the text version of the binary using default java charset";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private final static AmiAbstractMemberMethod<Bytes> TO_TEXT2 = new AmiAbstractMemberMethod<Bytes>(Bytes.class, "toText", String.class, String.class) {
		@Override
		public String invokeMethod2(CalcFrameStack sf, Bytes targetObject, Object[] params, DerivedCellCalculator caller) {
			String charsetName = (String) params[0];
			return new String(targetObject.getBytes(), Charset.forName(charsetName));
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "charset" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "charset" };
		}
		@Override
		protected String getHelp() {
			return "Tries to return the text version of the binary using the provided java charset";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	protected Object getCustomDebugProperty(String name, Bytes value) {
		if ("size".equals(name))
			return value.getBytes().length;
		if ("bytes".equals(name)) {
			ArrayList<Byte> r = new ArrayList<Byte>(value.getBytes().length);
			for (byte b : value.getBytes())
				r.add(b);
			return r;
		}
		return super.getCustomDebugProperty(name, value);
	}

	private final static AmiAbstractMemberMethod<Bytes> GET_LENGTH = new AmiAbstractMemberMethod<Bytes>(Bytes.class, "getLength", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Bytes targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.length();
		}
		@Override
		protected String getHelp() {
			return "returns number of bytes in this binary object";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Binary";
	}
	@Override
	public String getVarTypeDescription() {
		return "Binary stream of bytes";
	}
	@Override
	public Class<Bytes> getVarType() {
		return Bytes.class;
	}
	@Override
	public Class<Bytes> getVarDefaultImpl() {
		return null;
	}

	public static final AmiScriptMemberMethods_Binary INSTANCE = new AmiScriptMemberMethods_Binary();
}
