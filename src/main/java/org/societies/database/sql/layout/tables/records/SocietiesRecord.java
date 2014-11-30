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
public class SocietiesRecord extends org.jooq.impl.UpdatableRecordImpl<org.societies.database.sql.layout.tables.records.SocietiesRecord> implements org.jooq.Record5<byte[], java.lang.String, java.lang.String, java.lang.String, java.sql.Timestamp> {

	private static final long serialVersionUID = 525593058;

	/**
	 * Setter for <code>societies.societies.uuid</code>.
	 */
	public void setUuid(byte[] value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>societies.societies.uuid</code>.
	 */
	public byte[] getUuid() {
		return (byte[]) getValue(0);
	}

	/**
	 * Setter for <code>societies.societies.name</code>.
	 */
	public void setName(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>societies.societies.name</code>.
	 */
	public java.lang.String getName() {
		return (java.lang.String) getValue(1);
	}

	/**
	 * Setter for <code>societies.societies.tag</code>.
	 */
	public void setTag(java.lang.String value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>societies.societies.tag</code>.
	 */
	public java.lang.String getTag() {
		return (java.lang.String) getValue(2);
	}

	/**
	 * Setter for <code>societies.societies.clean_tag</code>.
	 */
	public void setCleanTag(java.lang.String value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>societies.societies.clean_tag</code>.
	 */
	public java.lang.String getCleanTag() {
		return (java.lang.String) getValue(3);
	}

	/**
	 * Setter for <code>societies.societies.created</code>.
	 */
	public void setCreated(java.sql.Timestamp value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>societies.societies.created</code>.
	 */
	public java.sql.Timestamp getCreated() {
		return (java.sql.Timestamp) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<byte[]> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<byte[], java.lang.String, java.lang.String, java.lang.String, java.sql.Timestamp> fieldsRow() {
		return (org.jooq.Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<byte[], java.lang.String, java.lang.String, java.lang.String, java.sql.Timestamp> valuesRow() {
		return (org.jooq.Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field1() {
		return org.societies.database.sql.layout.tables.Societies.SOCIETIES.UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return org.societies.database.sql.layout.tables.Societies.SOCIETIES.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field3() {
		return org.societies.database.sql.layout.tables.Societies.SOCIETIES.TAG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field4() {
		return org.societies.database.sql.layout.tables.Societies.SOCIETIES.CLEAN_TAG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field5() {
		return org.societies.database.sql.layout.tables.Societies.SOCIETIES.CREATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] value1() {
		return getUuid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value3() {
		return getTag();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value4() {
		return getCleanTag();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value5() {
		return getCreated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesRecord value1(byte[] value) {
		setUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesRecord value2(java.lang.String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesRecord value3(java.lang.String value) {
		setTag(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesRecord value4(java.lang.String value) {
		setCleanTag(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesRecord value5(java.sql.Timestamp value) {
		setCreated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesRecord values(byte[] value1, java.lang.String value2, java.lang.String value3, java.lang.String value4, java.sql.Timestamp value5) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached SocietiesRecord
	 */
	public SocietiesRecord() {
		super(org.societies.database.sql.layout.tables.Societies.SOCIETIES);
	}

	/**
	 * Create a detached, initialised SocietiesRecord
	 */
	public SocietiesRecord(byte[] uuid, java.lang.String name, java.lang.String tag, java.lang.String cleanTag, java.sql.Timestamp created) {
		super(org.societies.database.sql.layout.tables.Societies.SOCIETIES);

		setValue(0, uuid);
		setValue(1, name);
		setValue(2, tag);
		setValue(3, cleanTag);
		setValue(4, created);
	}
}