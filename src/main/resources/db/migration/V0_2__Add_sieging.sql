-- -----------------------------------------------------
-- Table `societies`.`cities`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `societies`.`cities` (
  `uuid`    VARBINARY(16) NOT NULL,
  `society` VARBINARY(16) NOT NULL,
  `x`       SMALLINT      NULL,
  `y`       SMALLINT      NULL,
  `z`       SMALLINT      NULL,
  PRIMARY KEY (`uuid`, `society`),
  UNIQUE INDEX `idcities_UNIQUE` (`uuid` ASC),
  INDEX `fk_cities_1_idx` (`society` ASC),
  CONSTRAINT `fk_cities_1`
  FOREIGN KEY (`society`)
  REFERENCES `societies`.`societies` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `societies`.`lands`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `societies`.`lands` (
  `uuid`   VARBINARY(16) NOT NULL,
  `origin` VARBINARY(16) NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE INDEX `idlands_UNIQUE` (`uuid` ASC),
  INDEX `fk_lands_1_idx` (`origin` ASC),
  CONSTRAINT `fk_lands_1`
  FOREIGN KEY (`origin`)
  REFERENCES `societies`.`cities` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `societies`.`sieges`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `societies`.`sieges` (
  `uuid`    VARBINARY(16) NOT NULL,
  `society` VARBINARY(16) NOT NULL,
  `city`    VARBINARY(16) NOT NULL,
  `x`       SMALLINT      NULL,
  `y`       SMALLINT      NULL,
  `z`       SMALLINT      NULL,
  `created` TIMESTAMP     NULL DEFAULT CURRENT_TIMESTAMP,
  `wager`   VARBINARY(16) NULL,
  PRIMARY KEY (`uuid`, `society`, `city`),
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC),
  INDEX `fk_sieges_1_idx` (`society` ASC),
  INDEX `fk_sieges_2_idx` (`city` ASC),
  CONSTRAINT `fk_sieges_1`
  FOREIGN KEY (`society`)
  REFERENCES `societies`.`societies` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_sieges_2`
  FOREIGN KEY (`city`)
  REFERENCES `societies`.`cities` (`uuid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE = InnoDB;
