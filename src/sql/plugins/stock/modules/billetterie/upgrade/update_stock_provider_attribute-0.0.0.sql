--
-- update les noms des attributs utilisés par l'application
--
UPDATE `billetterie`.`stock_provider_attribute` SET `attribute_key` = 'contactName1' WHERE  `stock_provider_attribute`.`attribute_key` = 'contactName';
UPDATE `billetterie`.`stock_provider_attribute` SET `attribute_key` = 'phoneNumber1' WHERE  `stock_provider_attribute`.`attribute_key` = 'phoneNumber';
UPDATE `billetterie`.`stock_provider_attribute` SET `attribute_key` = 'mail1' WHERE  `stock_provider_attribute`.`attribute_key` = 'mail';