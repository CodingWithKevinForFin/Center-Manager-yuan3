package com.f1.ami.relay.fh.tibcoems;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.tibco.tibjms.Tibjms;
import com.tibco.tibjms.TibjmsConnectionFactory;
import com.tibco.tibjms.naming.TibjmsContext;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.Session;

public class AmiTibcoEMSFH extends AmiFHBase implements ExceptionListener {
	private static final Logger log = LH.get();
	private static final String PROP_TABLE_NAME = "tableName";
	private static final String PROP_TABLE_MAPPING = "tableMapping";
	private static final String PROP_TOPIC = "topic";
	private static final String PROP_QUEUE = "queue";
	private static final String PROP_USE_TLS = "useTLS";
	private static final String PROP_USE_JNDI = "useJNDI";
	private static final String PROP_CLIENT_ID = "clientID";
	private static final String PROP_SERVER_URL = "serverUrl";
	private static final String PROP_USERNAME = "username";
	private static final String PROP_PASSWORD = "password";
	private static final String PROP_ACK_MODE = "ackMode";
	private Thread t = null;
	private String topic;
	private String queueName;
	private String table;
    String serverUrl = null;
    String userName = null;
    String password = null;
    boolean useTopic = true;
    int ackMode = Session.AUTO_ACKNOWLEDGE;
    Connection connection = null;
    Session session = null;
    MessageConsumer msgConsumer = null;
    Destination destination = null;
	Map<String, FHSetter> columnMapping;

	public AmiTibcoEMSFH() {
	}

	@Override
	public void start() {
		super.start();
		try {
			startFH();
			onStartFinish(true);
		} catch (Exception e) {
			log.severe("Failed to start up the FH: " + e.getMessage());
			onStartFinish(false);
		}
	}

	@SuppressWarnings("unchecked")
	private void parseMessage(final AmiRelayMapToBytesConverter converter, final ObjectToJsonConverter jsonConverter,
			final MapMessage message) throws Exception {
		converter.clear();
		if (log.isLoggable(Level.FINE))
			log.fine("Tibco EMS message received : " + message);

		if (columnMapping != null) {
			for (Map.Entry<String, FHSetter> e: columnMapping.entrySet()) {
				e.getValue().set(converter, message);
			}
		} else {
			Enumeration<String> en = message.getMapNames();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
			    Object o = message.getObject(key);
				if (o instanceof Double)
					converter.appendDouble(key, Caster_Double.INSTANCE.cast(o));
				else if (o instanceof Float)
					converter.appendFloat(key, Caster_Float.INSTANCE.cast(o));
				else if (o instanceof Byte)
					converter.appendByte(key, Caster_Byte.INSTANCE.cast(o));
				else if (o instanceof Boolean)
					converter.appendBoolean(key, Caster_Boolean.INSTANCE.cast(o));
				else if (o instanceof Character)
					converter.appendChar(key, Caster_Character.INSTANCE.cast(o));
				else if (o instanceof Short)
					converter.appendShort(key, Caster_Short.INSTANCE.cast(o));
				else if (o instanceof Integer)
					converter.appendInt(key, Caster_Integer.INSTANCE.cast(o));
				else if (o instanceof Long)
					converter.appendLong(key, Caster_Long.INSTANCE.cast(o));
				else
					converter.appendString(key, SH.toString(o));
			}
		}
		publishObjectToAmi(-1, message.getJMSMessageID(), table, 0, converter.toBytes());
	}

	private void startFH() {
		if (!SH.isEmpty(this.topic)) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						final AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
						final ObjectToJsonConverter jsonConverter = new ObjectToJsonConverter();
						
			            while (true) {
			                final MapMessage msg = (MapMessage) msgConsumer.receive();
			                if (msg == null) {
			                    break;
			                }
			                parseMessage(converter, jsonConverter, msg);
			                if (ackMode == Session.CLIENT_ACKNOWLEDGE
			                        || ackMode == Tibjms.EXPLICIT_CLIENT_ACKNOWLEDGE
			                        || ackMode == Tibjms.EXPLICIT_CLIENT_DUPS_OK_ACKNOWLEDGE) {
			                    msg.acknowledge();
			                }
			            }
			        } catch (Exception e) {
			            log.severe(e.toString());
						onFailed("Connection to Tipco EMS has been closed");
			        } finally {
						try {
							msgConsumer.close();
						} catch (JMSException e) {
							log.severe(e.toString());
						}
					}
				}
			};

			t = getManager().getThreadFactory().newThread(r);
			t.setDaemon(true);
			t.setName("TibcoEMS:Direct-" + this.getId());
			t.start();
		}
	}

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);

		this.serverUrl = props.getRequired(PROP_SERVER_URL);
		this.userName = props.getRequired(PROP_USERNAME);
		this.password = props.getRequired(PROP_PASSWORD);
		this.queueName = props.getOptional(PROP_QUEUE);
		
		if (!SH.is(this.queueName)) {
			this.topic = props.getRequired(PROP_TOPIC);
			this.useTopic = true;
		}
		if (SH.isEmpty(this.topic) && SH.isEmpty(this.queueName))
			throw new RuntimeException("Either a topic or queue name must be specified");
		
		this.table = props.getRequired(PROP_TABLE_NAME);

		String ackModeStr = SH.toLowerCase(props.getOptional(PROP_ACK_MODE));
		if ("auto".equals(ackModeStr))
			this.ackMode = Session.AUTO_ACKNOWLEDGE;
		else if ("client".equals(ackModeStr))
			this.ackMode = Session.CLIENT_ACKNOWLEDGE;
		else if ("dups_ok".equals(ackModeStr))
			this.ackMode = Session.DUPS_OK_ACKNOWLEDGE;
		else if ("explicit_client".equals(ackModeStr))
			this.ackMode = Tibjms.EXPLICIT_CLIENT_ACKNOWLEDGE;
		else if ("explicit_client_dups_ok".equals(ackModeStr))
			this.ackMode = Tibjms.EXPLICIT_CLIENT_DUPS_OK_ACKNOWLEDGE;
		else if ("no".equals(ackModeStr))
			this.ackMode = Tibjms.NO_ACKNOWLEDGE;
		else
			log.warning("Unknown ackMode passed in: " + ackModeStr);

		String tableMapping = props.getOptional(PROP_TABLE_MAPPING, "");
		if (!SH.isEmpty(tableMapping))
			buildTableSchema(tableMapping);

		boolean useTLS = props.getOptional(PROP_USE_TLS, false);
		if (useTLS)
			log.fine("Using TLS for connection");
		
		boolean useJNDI = props.getOptional(PROP_USE_JNDI, false);
		if (useJNDI)
			log.fine("Using JNDI");

		final String clientID = props.getOptional(PROP_CLIENT_ID, "EmsConsumer");
		Tibjms.setExceptionOnFTEvents(true);

        ConnectionFactory factory = null;
        Hashtable<String, Object> env = new Hashtable<String, Object>();

        try {
        	if (useJNDI) {
                env.put(Context.INITIAL_CONTEXT_FACTORY,
                        "com.tibco.tibjms.naming.TibjmsInitialContextFactory");
                env.put(Context.PROVIDER_URL, this.serverUrl);
                env.put(Context.URL_PKG_PREFIXES, "com.tibco.tibjms.naming");
                if (useTLS)
                    env.put(TibjmsContext.SECURITY_PROTOCOL, "ssl");
                env.put(Context.SECURITY_PRINCIPAL, this.userName);
                env.put(Context.SECURITY_CREDENTIALS, this.password);
                Context context = new InitialContext(env);
                Object factory1 = context.lookup("SSLFTGenericConnectionFactory");
                if (!(factory1 instanceof ConnectionFactory))
                    throw new NamingException(
                            "Expected ConnectionFactory but found: " + factory1.getClass().getName());
                factory = (ConnectionFactory) factory1;
            } else {
                TibjmsConnectionFactory factory1 = new TibjmsConnectionFactory(
                        serverUrl, clientID, env);
                factory1.setConnAttemptCount(200);
                factory1.setConnAttemptDelay(850);
                factory1.setConnAttemptTimeout(20000);
                factory1.setReconnAttemptCount(200);
                factory1.setReconnAttemptDelay(850);
                factory1.setReconnAttemptTimeout(20000);
                factory = (ConnectionFactory) factory1;
            }

            this.connection = factory.createConnection(userName, password);
            if (useJNDI)
                this.connection.setClientID(clientID);

            this.session = this.connection.createSession(this.ackMode);
            this.connection.setExceptionListener(this);
            if (useTopic)
                this.destination = this.session.createTopic(this.topic);
            else
                this.destination = this.session.createQueue(this.queueName);
            this.msgConsumer = this.session.createConsumer(this.destination);
            this.connection.start();	
        } catch (Exception e) {
        	log.severe(e.toString());
        }
		
		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();
	}

	// Expected format: col1=int,col2=string,col3=double,...
	private void buildTableSchema(final String mapping) {
		columnMapping = new HashMap<String, FHSetter>();
		List<String> colMaps = SH.splitToList(",", mapping);
		for (String colMap : colMaps) {
			// Extract column name and column type
			List<String> colType = SH.splitToList("=", colMap);
			if (colType.size() != 2)
				throw new RuntimeException("Failed to parse column name and column type: " + colMap);
			final String columnName = SH.trim(colType.get(0));
			final String columnType = SH.toLowerCase(SH.trim(colType.get(1)));
			switch (columnType) {
				case "string":
				case "str":
					columnMapping.put(columnName, new String_FHSetter(columnName));
					break;
				case "integer":
				case "int":
					columnMapping.put(columnName, new Int_FHSetter(columnName));
					break;
				case "short":
					columnMapping.put(columnName, new Short_FHSetter(columnName));
					break;
				case "long":
					columnMapping.put(columnName, new Long_FHSetter(columnName));
					break;
				case "float":
					columnMapping.put(columnName, new Float_FHSetter(columnName));
					break;
				case "double":
					columnMapping.put(columnName, new Double_FHSetter(columnName));
					break;
				case "char":
				case "character":
					columnMapping.put(columnName, new Char_FHSetter(columnName));
					break;
				case "bool":
				case "boolean":
					columnMapping.put(columnName, new Bool_FHSetter(columnName));
					break;
				default:
					throw new UnsupportedOperationException("Unsupported column type: " + columnType);
			}
		}
	}

	// Setter classes
	private static class Double_FHSetter extends FHSetter {

		public Double_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final MapMessage msg) throws JMSException {
			c.appendDouble(key, msg.getDouble(this.key));
		}
	}

	private static class Float_FHSetter extends FHSetter {

		public Float_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final MapMessage msg) throws JMSException {
			c.appendFloat(key, msg.getFloat(this.key));
		}
	}

	private static class Long_FHSetter extends FHSetter {

		public Long_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final MapMessage msg) throws JMSException {
			c.appendLong(key, msg.getLong(this.key));
		}
	}

	private static class Short_FHSetter extends FHSetter {

		public Short_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final MapMessage msg) throws JMSException {
			c.appendShort(key, msg.getShort(this.key));
		}
	}

	private static class Bool_FHSetter extends FHSetter {

		public Bool_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final MapMessage msg) throws JMSException {
			c.appendBoolean(key, msg.getBoolean(this.key));
		}
	}

	private static class Char_FHSetter extends FHSetter {

		public Char_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final MapMessage msg) throws JMSException {
			c.appendChar(key, msg.getChar(this.key));
		}
	}

	private static class Int_FHSetter extends FHSetter {

		public Int_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final MapMessage msg) throws JMSException {
			c.appendInt(key, msg.getInt(this.key));
		}
	}

	private static class String_FHSetter extends FHSetter {

		public String_FHSetter(final String key) {
			super(key);
		}

		public void set(final AmiRelayMapToBytesConverter c, final MapMessage msg) throws JMSException {
			c.appendString(key, msg.getString(this.key));
		}
	}

	private static abstract class FHSetter {

		final String key;

		public FHSetter(final String key) {
			this.key = key;
		}

		public abstract void set(final AmiRelayMapToBytesConverter c, final MapMessage msg) throws JMSException;
	}

	@Override
	public void onException(JMSException exception) {
		log.severe(exception.toString());	
	}

}