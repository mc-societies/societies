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
public class LandsRecord extends org.jooq.impl.UpdatableRecordImpl<org.societies.database.sql.layout.tables.records.LandsRecord> implements org.jooq.Record2<java.util.UUID, java.util.UUID> {

	private static final long serialVersionUID = -1850562388;

	/**
	 * Setter for <code>societies.lands.uuid</code>.
	 */
	public void setUuid(java.util.UUID value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>societies.lands.uuid</code>.
	 */
	public java.util.UUID getUuid() {
		return (java.util.UUID) getValue(0);
	}

	/**
	 * Setter for <code>societies.lands.origin</code>.
	 */
	public void setOrigin(java.util.UUID value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>societies.lands.origin</code>.
	 */
	public java.util.UUID getOrigin() {
		return (java.util.UUID) getValue(1);
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
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.util.UUID, java.util.UUID> fieldsRow() {
		return (org.jooq.Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.util.UUID, java.util.UUID> valuesRow() {
		return (org.jooq.Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field1() {
		return org.societies.database.sql.layout.tables.Lands.LANDS.UUID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.util.UUID> field2() {
		return org.societies.database.sql.layout.tables.Lands.LANDS.ORIGIN;
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
	public java.util.UUID value2() {
		return getOrigin();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LandsRecord value1(java.util.UUID value) {
		setUuid(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LandsRecord value2(java.util.UUID value) {
		setOrigin(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LandsRecord values(java.util.UUID value1, java.util.UUID value2) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached LandsRecord
	 */
	public LandsRecord() {
		super(org.societies.database.sql.layout.tables.Lands.LANDS);
	}

	/**
	 * Create a detached, initialised LandsRecord
	 */
	public LandsRecord(java.util.UUID uuid, java.util.UUID origin) {
		super(org.societies.database.sql.layout.tables.Lands.LANDS);

		setValue(0, uuid);
		setValue(1, origin);
	}
}