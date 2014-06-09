/**
 * This class is generated by jOOQ
 */
package net.catharos.societies.database.layout.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.3.2" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MembersRecord extends org.jooq.impl.UpdatableRecordImpl<net.catharos.societies.database.layout.tables.records.MembersRecord> implements org.jooq.Record3<byte[], java.sql.Timestamp, java.lang.Short> {

	private static final long serialVersionUID = -1427021721;

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
	 * Setter for <code>societies.members.state</code>.
	 */
	public void setState(java.lang.Short value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>societies.members.state</code>.
	 */
	public java.lang.Short getState() {
		return (java.lang.Short) getValue(2);
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
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<byte[], java.sql.Timestamp, java.lang.Short> fieldsRow() {
		return (org.jooq.Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row3<byte[], java.sql.Timestamp, java.lang.Short> valuesRow() {
		return (org.jooq.Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<byte[]> field1() {
		return net.catharos.societies.database.layout.tables.Members.MEMBERS.UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.sql.Timestamp> field2() {
		return net.catharos.societies.database.layout.tables.Members.MEMBERS.CREATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Short> field3() {
		return net.catharos.societies.database.layout.tables.Members.MEMBERS.STATE;
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
	public java.lang.Short value3() {
		return getState();
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
	public MembersRecord value3(java.lang.Short value) {
		setState(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord values(byte[] value1, java.sql.Timestamp value2, java.lang.Short value3) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached MembersRecord
	 */
	public MembersRecord() {
		super(net.catharos.societies.database.layout.tables.Members.MEMBERS);
	}

	/**
	 * Create a detached, initialised MembersRecord
	 */
	public MembersRecord(byte[] uuid, java.sql.Timestamp created, java.lang.Short state) {
		super(net.catharos.societies.database.layout.tables.Members.MEMBERS);

		setValue(0, uuid);
		setValue(1, created);
		setValue(2, state);
	}
}
