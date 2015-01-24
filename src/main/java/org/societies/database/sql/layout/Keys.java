/**
 * This class is generated by jOOQ
 */
package org.societies.database.sql.layout;

/**
 * A class modelling foreign key relationships between tables of the <code>societies</code> 
 * schema
 */
@javax.annotation.Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.5.1"
	},
	comments = "This class is generated by jOOQ"
)
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

	// -------------------------------------------------------------------------
	// IDENTITY definitions
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// UNIQUE and PRIMARY KEY definitions
	// -------------------------------------------------------------------------

	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.CitiesRecord> KEY_CITIES_PRIMARY = UniqueKeys0.KEY_CITIES_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.CitiesRecord> KEY_CITIES_IDCITIES_UNIQUE = UniqueKeys0.KEY_CITIES_IDCITIES_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.LandsRecord> KEY_LANDS_PRIMARY = UniqueKeys0.KEY_LANDS_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.LandsRecord> KEY_LANDS_IDLANDS_UNIQUE = UniqueKeys0.KEY_LANDS_IDLANDS_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.MembersRecord> KEY_MEMBERS_PRIMARY = UniqueKeys0.KEY_MEMBERS_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.MembersRecord> KEY_MEMBERS_UUID_UNIQUE = UniqueKeys0.KEY_MEMBERS_UUID_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.MembersRanksRecord> KEY_MEMBERS_RANKS_PRIMARY = UniqueKeys0.KEY_MEMBERS_RANKS_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.MemberSettingsRecord> KEY_MEMBER_SETTINGS_PRIMARY = UniqueKeys0.KEY_MEMBER_SETTINGS_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RanksRecord> KEY_RANKS_PRIMARY = UniqueKeys0.KEY_RANKS_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RanksRecord> KEY_RANKS_NAME_UNIQUE = UniqueKeys0.KEY_RANKS_NAME_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord> KEY_RANKS_SETTINGS_PRIMARY = UniqueKeys0.KEY_RANKS_SETTINGS_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RelationsRecord> KEY_RELATIONS_PRIMARY = UniqueKeys0.KEY_RELATIONS_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SchemaVersionRecord> KEY_SCHEMA_VERSION_PRIMARY = UniqueKeys0.KEY_SCHEMA_VERSION_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SiegesRecord> KEY_SIEGES_PRIMARY = UniqueKeys0.KEY_SIEGES_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SiegesRecord> KEY_SIEGES_UUID_UNIQUE = UniqueKeys0.KEY_SIEGES_UUID_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_PRIMARY = UniqueKeys0.KEY_SOCIETIES_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_UUID_UNIQUE = UniqueKeys0.KEY_SOCIETIES_UUID_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_NAME_UNIQUE = UniqueKeys0.KEY_SOCIETIES_NAME_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_TAG_UNIQUE = UniqueKeys0.KEY_SOCIETIES_TAG_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_CLEAN_TAG_UNIQUE = UniqueKeys0.KEY_SOCIETIES_CLEAN_TAG_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord> KEY_SOCIETIES_LOCKS_PRIMARY = UniqueKeys0.KEY_SOCIETIES_LOCKS_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord> KEY_SOCIETIES_LOCKS_ID_UNIQUE = UniqueKeys0.KEY_SOCIETIES_LOCKS_ID_UNIQUE;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRanksRecord> KEY_SOCIETIES_RANKS_PRIMARY = UniqueKeys0.KEY_SOCIETIES_RANKS_PRIMARY;
	public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord> KEY_SOCIETIES_SETTINGS_PRIMARY = UniqueKeys0.KEY_SOCIETIES_SETTINGS_PRIMARY;

	// -------------------------------------------------------------------------
	// FOREIGN KEY definitions
	// -------------------------------------------------------------------------

	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.CitiesRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_CITIES_1 = ForeignKeys0.FK_CITIES_1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.LandsRecord, org.societies.database.sql.layout.tables.records.CitiesRecord> FK_LANDS_1 = ForeignKeys0.FK_LANDS_1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MembersRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_MEMBERS_SOCIETIES1 = ForeignKeys0.FK_MEMBERS_SOCIETIES1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MembersRanksRecord, org.societies.database.sql.layout.tables.records.MembersRecord> FK_MEMBERS_HAS_RANKS_MEMBERS1 = ForeignKeys0.FK_MEMBERS_HAS_RANKS_MEMBERS1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MembersRanksRecord, org.societies.database.sql.layout.tables.records.RanksRecord> FK_MEMBERS_HAS_RANKS_RANKS1 = ForeignKeys0.FK_MEMBERS_HAS_RANKS_RANKS1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MemberSettingsRecord, org.societies.database.sql.layout.tables.records.MembersRecord> FK_SOCIETIES_MEMBERS_MEMBERS1 = ForeignKeys0.FK_SOCIETIES_MEMBERS_MEMBERS1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MemberSettingsRecord, org.societies.database.sql.layout.tables.records.MembersRecord> FK_SOCIETIES_MEMBERS_MEMBERS2 = ForeignKeys0.FK_SOCIETIES_MEMBERS_MEMBERS2;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, org.societies.database.sql.layout.tables.records.RanksRecord> FK_RANKS_SETTINGS_RANKS1 = ForeignKeys0.FK_RANKS_SETTINGS_RANKS1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, org.societies.database.sql.layout.tables.records.RanksRecord> FK_RANKS_SETTINGS_RANKS2 = ForeignKeys0.FK_RANKS_SETTINGS_RANKS2;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RelationsRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIETIES_HAS_SOCIETIES_SOCIETIES1 = ForeignKeys0.FK_SOCIETIES_HAS_SOCIETIES_SOCIETIES1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RelationsRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIETIES_HAS_SOCIETIES_SOCIETIES2 = ForeignKeys0.FK_SOCIETIES_HAS_SOCIETIES_SOCIETIES2;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SiegesRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SIEGES_1 = ForeignKeys0.FK_SIEGES_1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SiegesRecord, org.societies.database.sql.layout.tables.records.CitiesRecord> FK_SIEGES_2 = ForeignKeys0.FK_SIEGES_2;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesRanksRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIETIES_HAS_RANKS_SOCIETIES2 = ForeignKeys0.FK_SOCIETIES_HAS_RANKS_SOCIETIES2;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesRanksRecord, org.societies.database.sql.layout.tables.records.RanksRecord> FK_SOCIETIES_HAS_RANKS_RANKS2 = ForeignKeys0.FK_SOCIETIES_HAS_RANKS_RANKS2;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIEITES_SETTINGS_SOCIETIES1 = ForeignKeys0.FK_SOCIEITES_SETTINGS_SOCIETIES1;
	public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIETIES_SETTINGS_SOCIETIES1 = ForeignKeys0.FK_SOCIETIES_SETTINGS_SOCIETIES1;

	// -------------------------------------------------------------------------
	// [#1459] distribute members to avoid static initialisers > 64kb
	// -------------------------------------------------------------------------

	private static class UniqueKeys0 extends org.jooq.impl.AbstractKeys {
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.CitiesRecord> KEY_CITIES_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.Cities.CITIES, org.societies.database.sql.layout.tables.Cities.CITIES.UUID, org.societies.database.sql.layout.tables.Cities.CITIES.SOCIETY);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.CitiesRecord> KEY_CITIES_IDCITIES_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.Cities.CITIES, org.societies.database.sql.layout.tables.Cities.CITIES.UUID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.LandsRecord> KEY_LANDS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.Lands.LANDS, org.societies.database.sql.layout.tables.Lands.LANDS.UUID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.LandsRecord> KEY_LANDS_IDLANDS_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.Lands.LANDS, org.societies.database.sql.layout.tables.Lands.LANDS.UUID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.MembersRecord> KEY_MEMBERS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.Members.MEMBERS, org.societies.database.sql.layout.tables.Members.MEMBERS.UUID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.MembersRecord> KEY_MEMBERS_UUID_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.Members.MEMBERS, org.societies.database.sql.layout.tables.Members.MEMBERS.UUID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.MembersRanksRecord> KEY_MEMBERS_RANKS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.MembersRanks.MEMBERS_RANKS, org.societies.database.sql.layout.tables.MembersRanks.MEMBERS_RANKS.MEMBER, org.societies.database.sql.layout.tables.MembersRanks.MEMBERS_RANKS.RANK);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.MemberSettingsRecord> KEY_MEMBER_SETTINGS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS, org.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS.SUBJECT_UUID, org.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS.TARGET_UUID, org.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS.SETTING);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RanksRecord> KEY_RANKS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.Ranks.RANKS, org.societies.database.sql.layout.tables.Ranks.RANKS.UUID, org.societies.database.sql.layout.tables.Ranks.RANKS.NAME);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RanksRecord> KEY_RANKS_NAME_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.Ranks.RANKS, org.societies.database.sql.layout.tables.Ranks.RANKS.NAME);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord> KEY_RANKS_SETTINGS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS, org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS.SUBJECT_UUID, org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS.TARGET_UUID, org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS.SETTING);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.RelationsRecord> KEY_RELATIONS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.Relations.RELATIONS, org.societies.database.sql.layout.tables.Relations.RELATIONS.SOCIETY, org.societies.database.sql.layout.tables.Relations.RELATIONS.SOCIETY2);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SchemaVersionRecord> KEY_SCHEMA_VERSION_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.SchemaVersion.SCHEMA_VERSION, org.societies.database.sql.layout.tables.SchemaVersion.SCHEMA_VERSION.VERSION);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SiegesRecord> KEY_SIEGES_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.Sieges.SIEGES, org.societies.database.sql.layout.tables.Sieges.SIEGES.UUID, org.societies.database.sql.layout.tables.Sieges.SIEGES.SOCIETY, org.societies.database.sql.layout.tables.Sieges.SIEGES.CITY);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SiegesRecord> KEY_SIEGES_UUID_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.Sieges.SIEGES, org.societies.database.sql.layout.tables.Sieges.SIEGES.UUID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.Societies.SOCIETIES, org.societies.database.sql.layout.tables.Societies.SOCIETIES.UUID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_UUID_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.Societies.SOCIETIES, org.societies.database.sql.layout.tables.Societies.SOCIETIES.UUID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_NAME_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.Societies.SOCIETIES, org.societies.database.sql.layout.tables.Societies.SOCIETIES.NAME);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_TAG_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.Societies.SOCIETIES, org.societies.database.sql.layout.tables.Societies.SOCIETIES.TAG);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRecord> KEY_SOCIETIES_CLEAN_TAG_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.Societies.SOCIETIES, org.societies.database.sql.layout.tables.Societies.SOCIETIES.CLEAN_TAG);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord> KEY_SOCIETIES_LOCKS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.SocietiesLocks.SOCIETIES_LOCKS, org.societies.database.sql.layout.tables.SocietiesLocks.SOCIETIES_LOCKS.ID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesLocksRecord> KEY_SOCIETIES_LOCKS_ID_UNIQUE = createUniqueKey(org.societies.database.sql.layout.tables.SocietiesLocks.SOCIETIES_LOCKS, org.societies.database.sql.layout.tables.SocietiesLocks.SOCIETIES_LOCKS.ID);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesRanksRecord> KEY_SOCIETIES_RANKS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.SocietiesRanks.SOCIETIES_RANKS, org.societies.database.sql.layout.tables.SocietiesRanks.SOCIETIES_RANKS.SOCIETY, org.societies.database.sql.layout.tables.SocietiesRanks.SOCIETIES_RANKS.RANK);
		public static final org.jooq.UniqueKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord> KEY_SOCIETIES_SETTINGS_PRIMARY = createUniqueKey(org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS, org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS.SUBJECT_UUID, org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS.SETTING, org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS.TARGET_UUID);
	}

	private static class ForeignKeys0 extends org.jooq.impl.AbstractKeys {
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.CitiesRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_CITIES_1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY, org.societies.database.sql.layout.tables.Cities.CITIES, org.societies.database.sql.layout.tables.Cities.CITIES.SOCIETY);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.LandsRecord, org.societies.database.sql.layout.tables.records.CitiesRecord> FK_LANDS_1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_CITIES_PRIMARY, org.societies.database.sql.layout.tables.Lands.LANDS, org.societies.database.sql.layout.tables.Lands.LANDS.ORIGIN);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MembersRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_MEMBERS_SOCIETIES1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY, org.societies.database.sql.layout.tables.Members.MEMBERS, org.societies.database.sql.layout.tables.Members.MEMBERS.SOCIETY);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MembersRanksRecord, org.societies.database.sql.layout.tables.records.MembersRecord> FK_MEMBERS_HAS_RANKS_MEMBERS1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_MEMBERS_PRIMARY, org.societies.database.sql.layout.tables.MembersRanks.MEMBERS_RANKS, org.societies.database.sql.layout.tables.MembersRanks.MEMBERS_RANKS.MEMBER);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MembersRanksRecord, org.societies.database.sql.layout.tables.records.RanksRecord> FK_MEMBERS_HAS_RANKS_RANKS1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_RANKS_PRIMARY, org.societies.database.sql.layout.tables.MembersRanks.MEMBERS_RANKS, org.societies.database.sql.layout.tables.MembersRanks.MEMBERS_RANKS.RANK);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MemberSettingsRecord, org.societies.database.sql.layout.tables.records.MembersRecord> FK_SOCIETIES_MEMBERS_MEMBERS1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_MEMBERS_PRIMARY, org.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS, org.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS.SUBJECT_UUID);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.MemberSettingsRecord, org.societies.database.sql.layout.tables.records.MembersRecord> FK_SOCIETIES_MEMBERS_MEMBERS2 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_MEMBERS_PRIMARY, org.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS, org.societies.database.sql.layout.tables.MemberSettings.MEMBER_SETTINGS.TARGET_UUID);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, org.societies.database.sql.layout.tables.records.RanksRecord> FK_RANKS_SETTINGS_RANKS1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_RANKS_PRIMARY, org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS, org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS.SUBJECT_UUID);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RanksSettingsRecord, org.societies.database.sql.layout.tables.records.RanksRecord> FK_RANKS_SETTINGS_RANKS2 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_RANKS_PRIMARY, org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS, org.societies.database.sql.layout.tables.RanksSettings.RANKS_SETTINGS.TARGET_UUID);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RelationsRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIETIES_HAS_SOCIETIES_SOCIETIES1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY, org.societies.database.sql.layout.tables.Relations.RELATIONS, org.societies.database.sql.layout.tables.Relations.RELATIONS.SOCIETY);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.RelationsRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIETIES_HAS_SOCIETIES_SOCIETIES2 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY, org.societies.database.sql.layout.tables.Relations.RELATIONS, org.societies.database.sql.layout.tables.Relations.RELATIONS.SOCIETY2);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SiegesRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SIEGES_1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY, org.societies.database.sql.layout.tables.Sieges.SIEGES, org.societies.database.sql.layout.tables.Sieges.SIEGES.SOCIETY);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SiegesRecord, org.societies.database.sql.layout.tables.records.CitiesRecord> FK_SIEGES_2 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_CITIES_PRIMARY, org.societies.database.sql.layout.tables.Sieges.SIEGES, org.societies.database.sql.layout.tables.Sieges.SIEGES.CITY);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesRanksRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIETIES_HAS_RANKS_SOCIETIES2 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY, org.societies.database.sql.layout.tables.SocietiesRanks.SOCIETIES_RANKS, org.societies.database.sql.layout.tables.SocietiesRanks.SOCIETIES_RANKS.SOCIETY);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesRanksRecord, org.societies.database.sql.layout.tables.records.RanksRecord> FK_SOCIETIES_HAS_RANKS_RANKS2 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_RANKS_PRIMARY, org.societies.database.sql.layout.tables.SocietiesRanks.SOCIETIES_RANKS, org.societies.database.sql.layout.tables.SocietiesRanks.SOCIETIES_RANKS.RANK);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIEITES_SETTINGS_SOCIETIES1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY, org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS, org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS.SUBJECT_UUID);
		public static final org.jooq.ForeignKey<org.societies.database.sql.layout.tables.records.SocietiesSettingsRecord, org.societies.database.sql.layout.tables.records.SocietiesRecord> FK_SOCIETIES_SETTINGS_SOCIETIES1 = createForeignKey(org.societies.database.sql.layout.Keys.KEY_SOCIETIES_PRIMARY, org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS, org.societies.database.sql.layout.tables.SocietiesSettings.SOCIETIES_SETTINGS.TARGET_UUID);
	}
}
