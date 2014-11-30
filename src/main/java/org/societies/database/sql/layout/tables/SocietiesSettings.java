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
public class SocietiesSettings extends org.jooq.impl.TableImpl<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord> {

	private static final long serialVersionUID = 1776522936;

	/**
	 * The singleton instance of <code>societies.societies_settings</code>
	 */
	public static final org.societies.database.sql.layout.tables.SocietiesSettings SOCIETIES_SETTINGS = new org.societies.database.sql.layout.tables.SocietiesSettings();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord> getRecordType() {
		return org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord.class;
	}

	/**
	 * The column <code>societies.societies_settings.subject_uuid</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, byte[]> SUBJECT_UUID = createField("subject_uuid", org.jooq.impl.SQLDataType.VARBINARY.length(16).nullable(false), this, "");

	/**
	 * The column <code>societies.societies_settings.target_uuid</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, byte[]> TARGET_UUID = createField("target_uuid", org.jooq.impl.SQLDataType.VARBINARY.length(16).nullable(false), this, "");

	/**
	 * The column <code>societies.societies_settings.setting</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, org.jooq.types.UShort> SETTING = createField("setting", org.jooq.impl.SQLDataType.SMALLINTUNSIGNED.nullable(false), this, "");

	/**
	 * The column <code>societies.societies_settings.value</code>.
	 */
	public final org.jooq.TableField<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, byte[]> VALUE = createField("value", org.jooq.impl.SQLDataType.VARBINARY.length(64), this, "");

	/**
	 * Create a <code>societies.societies_settings</code> table reference
	 */
	public SocietiesSettings() {
		this("societies_settings", null);
	}

	/**
	 * Create an aliased <code>societies.societies_settings</code> table reference
	 */
	public SocietiesSettings(java.lang.String alias) {
		this(alias, org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS);
	}

	private SocietiesSettings(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord> aliased) {
		this(alias, aliased, null);
	}

	private SocietiesSettings(java.lang.String alias, org.jooq.Table<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, org.societies.database.sql.layout.Societies.SOCIETIES, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord> getPrimaryKey() {
		return org.societies.database.sql.layout.Keys.KEY_SOCIETIES_SETTINGS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord>>asList(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_SETTINGS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, ?>>asList(org.societies.database.sql.layout.Keys.FK_SOCIEITES_SETTINGS_SOCIETIES1, org.societies.database.sql.layout.Keys.FK_SOCIETIES_SETTINGS_SOCIETIES1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.societies.database.sql.layout.tables.SocietiesSettings as(java.lang.String alias) {
		return new org.societies.database.sql.layout.tables.SocietiesSettings(alias, this);
	}

	/**
	 * Rename this table
	 */
	public org.societies.database.sql.layout.tables.SocietiesSettings rename(java.lang.String name) {
		return new org.societies.database.sql.layout.tables.SocietiesSettings(name, null);
	}
}