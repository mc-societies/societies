ALTER TABLE `societies`.`member_settings`
DROP FOREIGN KEY `fk_societies_members_members1`;

ALTER TABLE `societies`.`member_settings`
DROP FOREIGN KEY `fk_societies_members_members2`;

ALTER TABLE `societies`.`member_settings`
ADD CONSTRAINT `fk_societies_members_members1`
FOREIGN KEY (`subject_uuid`)
REFERENCES `societies`.`members` (`uuid`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

ALTER TABLE `societies`.`member_settings`
ADD CONSTRAINT `fk_societies_members_members2`
FOREIGN KEY (`target_uuid`)
REFERENCES `societies`.`members` (`uuid`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;



