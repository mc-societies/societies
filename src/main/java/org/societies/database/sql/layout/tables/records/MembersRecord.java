/**
 * This class is generated by jOOQ
 */
package org.societies.database.sql.layout.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.5.1"
	},
	comments = "This class is generated by jOOQ"
)
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MembersRecord extends org.jooq.impl.UpdatableRecordImpl<org.societies.database.sql.layout.tables.records.MembersRecord> implements org.jooq.Record4<java.util.UUID, org.joda.time.DateTime, java.util.UUID, org.joda.time.DateTime> {

	private static final long serialVersionUID = -1610355934;

	/**
	 * Setter for <code>societies.members.uuid</code>.
	 */
	public void setUuid(java.util.UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>societies.members.uuid</code>.
	 */
	public java.util.UUID getUuid() {
		return (java.util.UUID) getValue(0);
	}

	/**
	 * Setter for <code>societies.members.created</code>.
	 */
	public void setCreated(org.joda.time.DateTime value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>societies.members.created</code>.
	 */
	public org.joda.time.DateTime getCreated() {
		return (org.joda.time.DateTime) getValue(1);
	}

	/**
	 * Setter for <code>societies.members.society</code>.
	 */
	public void setSociety(java.util.UUID value) {
		setValue(2, value);
	}

	/**
	 * Getter for <code>societies.members.society</code>.
	 */
	public java.util.UUID getSociety() {
		return (java.util.UUID) getValue(2);
	}

	/**
	 * Setter for <code>societies.members.lastActive</code>.
	 */
	public void setLastactive(org.joda.time.DateTime value) {
		setValue(3, value);
	}

	/**
	 * Getter for <code>societies.members.lastActive</code>.
	 */
	public org.joda.time.DateTime getLastactive() {
		return (org.joda.time.DateTime) getValue(3);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.util.UUID> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record4 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.util.UUID, org.joda.time.DateTime, java.util.UUID, org.joda.time.DateTime> fieldsRow() {
		return (org.jooq.Row4) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row4<java.util.UUID, org.joda.time.DateTime, java.util.UUID, org.joda.time.DateTime> valuesRow() {
		return (org.jooq.Row4) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field1() {
		return org.societies.database.sql.layout.tables.Members.MEMBERS.UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<org.joda.time.DateTime> field2() {
		return org.societies.database.sql.layout.tables.Members.MEMBERS.CREATED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field3() {
		return org.societies.database.sql.layout.tables.Members.MEMBERS.SOCIETY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<org.joda.time.DateTime> field4() {
		return org.societies.database.sql.layout.tables.Members.MEMBERS.LASTACTIVE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.UUID value1() {
		return getUuid();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.joda.time.DateTime value2() {
		return getCreated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.UUID value3() {
		return getSociety();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.joda.time.DateTime value4() {
		return getLastactive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord value1(java.util.UUID value) {
		setUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord value2(org.joda.time.DateTime value) {
		setCreated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord value3(java.util.UUID value) {
		setSociety(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord value4(org.joda.time.DateTime value) {
		setLastactive(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MembersRecord values(java.util.UUID value1, org.joda.time.DateTime value2, java.util.UUID value3, org.joda.time.DateTime value4) {
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
	public MembersRecord(java.util.UUID uuid, org.joda.time.DateTime created, java.util.UUID society, org.joda.time.DateTime lastactive) {
		super(org.societies.database.sql.layout.tables.Members.MEMBERS);

		setValue(0, uuid);
		setValue(1, created);
		setValue(2, society);
		setValue(3, lastactive);
	}
}
