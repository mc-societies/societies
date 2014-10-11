/**
 * This class is generated by jOOQ
 */
package net.catharos.societies.database.layout.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.3.2" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Societies extends org.jooq.impl.TableImpl<net.catharos.societies.database.layout.tables.records.SocietiesRecord> {

	private static final long serialVersionUID = -819694017;

	/**
	 * The singleton instance of <code>societies.societies</code>
	 */
	public static final net.catharos.societies.database.layout.tables.Societies SOCIETIES = new net.catharos.societies.database.layout.tables.Societies();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<net.catharos.societies.database.layout.tables.records.SocietiesRecord> getRecordType() {
		return net.catharos.societies.database.layout.tables.records.SocietiesRecord.class;
	}

	/**
	 * The column <code>societies.societies.uuid</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.layout.tables.records.SocietiesRecord, byte[]> UUID = createField("uuid", org.jooq.impl.SQLDataType.VARBINARY.length(16).nullable(false), this, "");

	/**
	 * The column <code>societies.societies.name</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.layout.tables.records.SocietiesRecord, java.lang.String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>societies.societies.tag</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.layout.tables.records.SocietiesRecord, java.lang.String> TAG = createField("tag", org.jooq.impl.SQLDataType.VARCHAR.length(45).nullable(false), this, "");

	/**
	 * The column <code>societies.societies.clean_tag</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.layout.tables.records.SocietiesRecord, java.lang.String> CLEAN_TAG = createField("clean_tag", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>societies.societies.created</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.layout.tables.records.SocietiesRecord, java.sql.Timestamp> CREATED = createField("created", org.jooq.impl.SQLDataType.TIMESTAMP.defaulted(true), this, "");

	/**
	 * The column <code>societies.societies.state</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.layout.tables.records.SocietiesRecord, java.lang.Short> STATE = createField("state", org.jooq.impl.SQLDataType.SMALLINT.defaulted(true), this, "");

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
		this(alias, net.catharos.societies.database.layout.tables.Societies.SOCIETIES);
	}

	private Societies(java.lang.String alias, org.jooq.Table<net.catharos.societies.database.layout.tables.records.SocietiesRecord> aliased) {
		this(alias, aliased, null);
	}

	private Societies(java.lang.String alias, org.jooq.Table<net.catharos.societies.database.layout.tables.records.SocietiesRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, net.catharos.societies.database.layout.Societies.SOCIETIES, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<net.catharos.societies.database.layout.tables.records.SocietiesRecord> getPrimaryKey() {
		return net.catharos.societies.database.layout.Keys.KEY_SOCIETIES_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<net.catharos.societies.database.layout.tables.records.SocietiesRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<net.catharos.societies.database.layout.tables.records.SocietiesRecord>>asList(net.catharos.societies.database.layout.Keys.KEY_SOCIETIES_PRIMARY, net.catharos.societies.database.layout.Keys.KEY_SOCIETIES_UUID_UNIQUE, net.catharos.societies.database.layout.Keys.KEY_SOCIETIES_NAME_UNIQUE, net.catharos.societies.database.layout.Keys.KEY_SOCIETIES_TAG_UNIQUE, net.catharos.societies.database.layout.Keys.KEY_SOCIETIES_CLEAN_TAG_UNIQUE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public net.catharos.societies.database.layout.tables.Societies as(java.lang.String alias) {
		return new net.catharos.societies.database.layout.tables.Societies(alias, this);
	}

	/**
	 * Rename this table
	 */
	public net.catharos.societies.database.layout.tables.Societies rename(java.lang.String name) {
		return new net.catharos.societies.database.layout.tables.Societies(name, null);
	}
}
