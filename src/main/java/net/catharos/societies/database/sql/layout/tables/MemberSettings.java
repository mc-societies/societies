/**
 * This class is generated by jOOQ
 */
package net.catharos.societies.database.sql.layout.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.4" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MemberSettings extends org.jooq.impl.TableImpl<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord> {

	private static final long serialVersionUID = -1476633043;

	/**
	 * The singleton instance of <code>societies.member_settings</code>
	 */
	public static final net.catharos.societies.database.sql.layout.tables.MemberSettings MEMBER_SETTINGS = new net.catharos.societies.database.sql.layout.tables.MemberSettings();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord> getRecordType() {
		return net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord.class;
	}

	/**
	 * The column <code>societies.member_settings.subject_uuid</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord, byte[]> SUBJECT_UUID = createField("subject_uuid", org.jooq.impl.SQLDataType.VARBINARY.length(16).nullable(false), this, "");

	/**
	 * The column <code>societies.member_settings.target_uuid</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord, byte[]> TARGET_UUID = createField("target_uuid", org.jooq.impl.SQLDataType.VARBINARY.length(16).nullable(false), this, "");

	/**
	 * The column <code>societies.member_settings.setting</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord, org.jooq.types.UShort> SETTING = createField("setting", org.jooq.impl.SQLDataType.SMALLINTUNSIGNED.nullable(false), this, "");

	/**
	 * The column <code>societies.member_settings.value</code>.
	 */
	public final org.jooq.TableField<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord, byte[]> VALUE = createField("value", org.jooq.impl.SQLDataType.VARBINARY.length(64), this, "");

	/**
	 * Create a <code>societies.member_settings</code> table reference
	 */
	public MemberSettings() {
		this("member_settings", null);
	}

	/**
	 * Create an aliased <code>societies.member_settings</code> table reference
	 */
	public MemberSettings(java.lang.String alias) {
		this(alias, net.catharos.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS);
	}

	private MemberSettings(java.lang.String alias, org.jooq.Table<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord> aliased) {
		this(alias, aliased, null);
	}

	private MemberSettings(java.lang.String alias, org.jooq.Table<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, net.catharos.societies.database.sql.layout.Societies.SOCIETIES, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord> getPrimaryKey() {
		return net.catharos.societies.database.sql.layout.Keys.KEY_MEMBER_SETTINGS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord>>asList(net.catharos.societies.database.sql.layout.Keys.KEY_MEMBER_SETTINGS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<net.catharos.societies.database.sql.layout.tables.records.MemberSettingsRecord, ?>>asList(net.catharos.societies.database.sql.layout.Keys.FK_SOCIETIES_MEMBERS_MEMBERS1, net.catharos.societies.database.sql.layout.Keys.FK_SOCIETIES_MEMBERS_MEMBERS2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public net.catharos.societies.database.sql.layout.tables.MemberSettings as(java.lang.String alias) {
		return new net.catharos.societies.database.sql.layout.tables.MemberSettings(alias, this);
	}

	/**
	 * Rename this table
	 */
	public net.catharos.societies.database.sql.layout.tables.MemberSettings rename(java.lang.String name) {
		return new net.catharos.societies.database.sql.layout.tables.MemberSettings(name, null);
	}
}
