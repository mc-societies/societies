/**
 * This class is generated by jOOQ
 */
package net.catharos.societies.database.sql.layout.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.4" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RanksSettingsRecord extends org.jooq.impl.UpdatableRecordImpl<net.catharos.societies.database.sql.layout.tables.records.RanksSettingsRecord> implements org.jooq.Record4<byte[], byte[], org.jooq.types.UShort, byte[]> {

	private static final long serialVersionUID = -620311207;

	/**
	 * Setter for <code>societies.ranks_settings.subject_uuid</code>.
	 */
	public void setSubjectUuid(byte[] value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>societies.ranks_settings.subject_uuid</code>.
	 */
	public byte[] getSubjectUuid() {
		return (byte[]) getValue(0);
	}

	/**
	 * Setter for <code>societies.ranks_settings.target_uuid</code>.
	 */
	public void setTargetUuid(byte[] value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>societies.ranks_settings.target_uuid</code>.
	 */
	public byte[] getTargetUuid() {
		return (byte[]) getValue(1);
	}

	/**
	 * Setter for <code>societies.ranks_settings.setting</code>.
	 */
	public void setSetting(org.jooq.types.UShort value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>societies.ranks_settings.setting</code>.
	 */
	public org.jooq.types.UShort getSetting() {
		return (org.jooq.types.UShort) getValue(2);
	}

	/**
	 * Setter for <code>societies.ranks_settings.value</code>.
	 */
	public void setValue(byte[] value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>societies.ranks_settings.value</code>.
	 */
	public byte[] getValue() {
		return (byte[]) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record3<byte[], byte[], org.jooq.types.UShort> key() {
		return (org.jooq.Record3) super.key();
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<byte[], byte[], org.jooq.types.UShort, byte[]> fieldsRow() {
		return (org.jooq.Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<byte[], byte[], org.jooq.types.UShort, byte[]> valuesRow() {
		return (org.jooq.Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field1() {
		return net.catharos.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS.SUBJECT_UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field2() {
		return net.catharos.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS.TARGET_UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<org.jooq.types.UShort> field3() {
		return net.catharos.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS.SETTING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field4() {
		return net.catharos.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS.VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] value1() {
		return getSubjectUuid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] value2() {
		return getTargetUuid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.types.UShort value3() {
		return getSetting();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] value4() {
		return getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RanksSettingsRecord value1(byte[] value) {
		setSubjectUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RanksSettingsRecord value2(byte[] value) {
		setTargetUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RanksSettingsRecord value3(org.jooq.types.UShort value) {
		setSetting(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RanksSettingsRecord value4(byte[] value) {
		setValue(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RanksSettingsRecord values(byte[] value1, byte[] value2, org.jooq.types.UShort value3, byte[] value4) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached RanksSettingsRecord
	 */
	public RanksSettingsRecord() {
		super(net.catharos.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS);
	}

	/**
	 * Create a detached, initialised RanksSettingsRecord
	 */
	public RanksSettingsRecord(byte[] subjectUuid, byte[] targetUuid, org.jooq.types.UShort setting, byte[] value) {
		super(net.catharos.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS);

		setValue(0, subjectUuid);
		setValue(1, targetUuid);
		setValue(2, setting);
		setValue(3, value);
	}
}
