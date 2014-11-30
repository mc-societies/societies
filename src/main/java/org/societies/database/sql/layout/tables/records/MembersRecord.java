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
public class MembersRecord extends org.jooq.impl.UpdatableRecordImpl<org.societies.database.sql.layout.tables.records.MembersRecord> implements org.jooq.Record4<byte[], java.sql.Timestamp, byte[], java.sql.Timestamp> {

	private static final long serialVersionUID = 546631371;

	/**
	 * Setter for <code>societies.members.uuid</code>.
	 */
	public void setUuid(byte[] value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>societies.members.uuid</code>.
	 */
	public byte[] getUuid() {
		return (byte[]) getValue(0);
	}

	/**
	 * Setter for <code>societies.members.created</code>.
	 */
	public void setCreated(java.sql.Timestamp value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>societies.members.created</code>.
	 */
	public java.sql.Timestamp getCreated() {
		return (java.sql.Timestamp) getValue(1);
	}

	/**
	 * Setter for <code>societies.members.society</code>.
	 */
	public void setSociety(byte[] value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>societies.members.society</code>.
	 */
	public byte[] getSociety() {
		return (byte[]) getValue(2);
	}

	/**
	 * Setter for <code>societies.members.lastActive</code>.
	 */
	public void setLastactive(java.sql.Timestamp value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>societies.members.lastActive</code>.
	 */
	public java.sql.Timestamp getLastactive() {
		return (java.sql.Timestamp) getValue(3);
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
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<byte[], java.sql.Timestamp, byte[], java.sql.Timestamp> fieldsRow() {
		return (org.jooq.Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<byte[], java.sql.Timestamp, byte[], java.sql.Timestamp> valuesRow() {
		return (org.jooq.Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field1() {
		return org.societies.database.sql.layout.tables.Members.MEMBERS.UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field2() {
		return org.societies.database.sql.layout.tables.Members.MEMBERS.CREATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field3() {
		return org.societies.database.sql.layout.tables.Members.MEMBERS.SOCIETY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field4() {
		return org.societies.database.sql.layout.tables.Members.MEMBERS.LASTACTIVE;
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
	public java.sql.Timestamp value2() {
		return getCreated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] value3() {
		return getSociety();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Timestamp value4() {
		return getLastactive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord value1(byte[] value) {
		setUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord value2(java.sql.Timestamp value) {
		setCreated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord value3(byte[] value) {
		setSociety(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord value4(java.sql.Timestamp value) {
		setLastactive(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord values(byte[] value1, java.sql.Timestamp value2, byte[] value3, java.sql.Timestamp value4) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached MembersRecord
	 */
	public MembersRecord() {
		super(org.societies.database.sql.layout.tables.Members.MEMBERS);
	}

	/**
	 * Create a detached, initialised MembersRecord
	 */
	public MembersRecord(byte[] uuid, java.sql.Timestamp created, byte[] society, java.sql.Timestamp lastactive) {
		super(org.societies.database.sql.layout.tables.Members.MEMBERS);

		setValue(0, uuid);
		setValue(1, created);
		setValue(2, society);
		setValue(3, lastactive);
	}
}