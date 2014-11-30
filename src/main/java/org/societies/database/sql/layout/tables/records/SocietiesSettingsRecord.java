/**
 * This class is generated by jOOQ
 */
package org.societies.database.sql.layout.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.4" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SocietiesSettingsRecord extends org.jooq.impl.UpdatableRecordImpl<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord> implements org.jooq.Record4<byte[], byte[], org.jooq.types.UShort, byte[]> {

	private static final long serialVersionUID = -1389248064;

	/**
	 * Setter for <code>societies.societies_settings.subject_uuid</code>.
	 */
	public void setSubjectUuid(byte[] value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>societies.societies_settings.subject_uuid</code>.
	 */
	public byte[] getSubjectUuid() {
		return (byte[]) getValue(0);
	}

	/**
	 * Setter for <code>societies.societies_settings.target_uuid</code>.
	 */
	public void setTargetUuid(byte[] value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>societies.societies_settings.target_uuid</code>.
	 */
	public byte[] getTargetUuid() {
		return (byte[]) getValue(1);
	}

	/**
	 * Setter for <code>societies.societies_settings.setting</code>.
	 */
	public void setSetting(org.jooq.types.UShort value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>societies.societies_settings.setting</code>.
	 */
	public org.jooq.types.UShort getSetting() {
		return (org.jooq.types.UShort) getValue(2);
	}

	/**
	 * Setter for <code>societies.societies_settings.value</code>.
	 */
	public void setValue(byte[] value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>societies.societies_settings.value</code>.
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
	public org.jooq.Record3<byte[], org.jooq.types.UShort, byte[]> key() {
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
		return org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS.SUBJECT_UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field2() {
		return org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS.TARGET_UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<org.jooq.types.UShort> field3() {
		return org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS.SETTING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field4() {
		return org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS.VALUE;
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
	public SocietiesSettingsRecord value1(byte[] value) {
		setSubjectUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesSettingsRecord value2(byte[] value) {
		setTargetUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesSettingsRecord value3(org.jooq.types.UShort value) {
		setSetting(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesSettingsRecord value4(byte[] value) {
		setValue(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesSettingsRecord values(byte[] value1, byte[] value2, org.jooq.types.UShort value3, byte[] value4) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached SocietiesSettingsRecord
	 */
	public SocietiesSettingsRecord() {
		super(org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS);
	}

	/**
	 * Create a detached, initialised SocietiesSettingsRecord
	 */
	public SocietiesSettingsRecord(byte[] subjectUuid, byte[] targetUuid, org.jooq.types.UShort setting, byte[] value) {
		super(org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS);

		setValue(0, subjectUuid);
		setValue(1, targetUuid);
		setValue(2, setting);
		setValue(3, value);
	}
}