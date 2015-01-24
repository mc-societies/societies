/**
 * This class is generated by jOOQ
 */
package org.societies.database.sql.layout.tables;

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
public class Societies extends org.jooq.impl.TableImpl<org.societies.database.sql.layout.tables.records.SocietiesRecord> {

	private static final long serialVersionUID = -1759179088;

	/**
	 * The reference instance of <code>societies.societies</code>
	 */
	public static final org.societies.database.sql.layout.tables.Societies SOCIETIES = new org.societies.database.sql.layout.tables.Societies();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.societies.database.sql.layout.tables.records.SocietiesRecord> getRecordType() {
		return org.societies.database.sql.layout.tables.records.SocietiesRecord.class;
	}

	/**
	 * The column <code>societies.societies.uuid</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesRecord, java.util.UUID> UUID = createField("uuid", org.jooq.impl.SQLDataType.VARBINARY.length(16).nullable(false), this, "", new org.societies.database.sql.UUIDConverter());

	/**
	 * The column <code>societies.societies.name</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesRecord, java.lang.String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>societies.societies.tag</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesRecord, java.lang.String> TAG = createField("tag", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>societies.societies.clean_tag</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesRecord, java.lang.String> CLEAN_TAG = createField("clean_tag", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>societies.societies.created</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesRecord, java.sql.Timestamp> CREATED = createField("created", org.jooq.impl.SQLDataType.TIMESTAMP.defaulted(true), this, "");

	/**
	 * Create a <code>societies.societies</code> table reference
	 */
	public Societies() {
		this("societies", null);
	}

	/**
	 * Create an aliased <code>societies.societies</code> table reference
	 */
	public Societies(java.lang.String alias) {
		this(alias, org.societies.database.sql.layout.tables.Societies.SOCIETIES);
	}

	private Societies(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.SocietiesRecord> aliased) {
		this(alias, aliased, null);
	}

	private Societies(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.SocietiesRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, org.societies.database.sql.layout.Societies.SOCIETIES, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> getPrimaryKey() {
		return org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord>>asList(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY, org.societies.database.sql.layout.Keys.KEY_SOCIETIES_UUID_UNIQUE, org.societies.database.sql.layout.Keys.KEY_SOCIETIES_NAME_UNIQUE, org.societies.database.sql.layout.Keys.KEY_SOCIETIES_TAG_UNIQUE, org.societies.database.sql.layout.Keys.KEY_SOCIETIES_CLEAN_TAG_UNIQUE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.societies.database.sql.layout.tables.Societies as(java.lang.String alias) {
		return new org.societies.database.sql.layout.tables.Societies(alias, this);
	}

	/**
	 * Rename this table
	 */
	public org.societies.database.sql.layout.tables.Societies rename(java.lang.String name) {
		return new org.societies.database.sql.layout.tables.Societies(name, null);
	}
}
