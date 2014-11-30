/**
 * This class is generated by jOOQ
 */
package org.societies.database.sql.layout.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.4" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SocietiesLocks extends org.jooq.impl.TableImpl<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord> {

	private static final long serialVersionUID = 1892509929;

	/**
	 * The singleton instance of <code>societies.societies_locks</code>
	 */
	public static final org.societies.database.sql.layout.tables.SocietiesLocks SOCIETIES_LOCKS = new org.societies.database.sql.layout.tables.SocietiesLocks();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord> getRecordType() {
		return org.societies.database.sql.layout.tables.records.SocietiesLocksRecord.class;
	}

	/**
	 * The column <code>societies.societies_locks.id</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord, java.lang.Short> ID = createField("id", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

	/**
	 * Create a <code>societies.societies_locks</code> table reference
	 */
	public SocietiesLocks() {
		this("societies_locks", null);
	}

	/**
	 * Create an aliased <code>societies.societies_locks</code> table reference
	 */
	public SocietiesLocks(java.lang.String alias) {
		this(alias, org.societies.database.sql.layout.tables.SocietiesLocks.SOCIETIES_LOCKS);
	}

	private SocietiesLocks(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord> aliased) {
		this(alias, aliased, null);
	}

	private SocietiesLocks(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, org.societies.database.sql.layout.Societies.SOCIETIES, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord> getPrimaryKey() {
		return org.societies.database.sql.layout.Keys.KEY_SOCIETIES_LOCKS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord>>asList(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_LOCKS_PRIMARY, org.societies.database.sql.layout.Keys.KEY_SOCIETIES_LOCKS_ID_UNIQUE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.societies.database.sql.layout.tables.SocietiesLocks as(java.lang.String alias) {
		return new org.societies.database.sql.layout.tables.SocietiesLocks(alias, this);
	}

	/**
	 * Rename this table
	 */
	public org.societies.database.sql.layout.tables.SocietiesLocks rename(java.lang.String name) {
		return new org.societies.database.sql.layout.tables.SocietiesLocks(name, null);
	}
}