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
public class SocietiesRecord extends org.jooq.impl.UpdatableRecordImpl<org.societies.database.sql.layout.tables.records.SocietiesRecord> implements org.jooq.Record5<java.util.UUID, java.lang.String, java.lang.String, java.lang.String, org.joda.time.DateTime> {

	private static final long serialVersionUID = -1878866858;

	/**
	 * Setter for <code>societies.societies.uuid</code>.
	 */
	public void setUuid(java.util.UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>societies.societies.uuid</code>.
	 */
	public java.util.UUID getUuid() {
		return (java.util.UUID) getValue(0);
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
	public void setCreated(org.joda.time.DateTime value) {
		setValue(4, value);
	}

	/**
	 * Getter for <code>societies.societies.created</code>.
	 */
	public org.joda.time.DateTime getCreated() {
		return (org.joda.time.DateTime) getValue(4);
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
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.util.UUID, java.lang.String, java.lang.String, java.lang.String, org.joda.time.DateTime> fieldsRow() {
		return (org.jooq.Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row5<java.util.UUID, java.lang.String, java.lang.String, java.lang.String, org.joda.time.DateTime> valuesRow() {
		return (org.jooq.Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field1() {
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
	public org.jooq.Field<org.joda.time.DateTime> field5() {
		return org.societies.database.sql.layout.tables.Societies.SOCIETIES.CREATED;
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
	public org.joda.time.DateTime value5() {
		return getCreated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesRecord value1(java.util.UUID value) {
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
	public SocietiesRecord value5(org.joda.time.DateTime value) {
		setCreated(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SocietiesRecord values(java.util.UUID value1, java.lang.String value2, java.lang.String value3, java.lang.String value4, org.joda.time.DateTime value5) {
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
	public SocietiesRecord(java.util.UUID uuid, java.lang.String name, java.lang.String tag, java.lang.String cleanTag, org.joda.time.DateTime created) {
		super(org.societies.database.sql.layout.tables.Societies.SOCIETIES);

		setValue(0, uuid);
		setValue(1, name);
		setValue(2, tag);
		setValue(3, cleanTag);
		setValue(4, created);
	}
}
