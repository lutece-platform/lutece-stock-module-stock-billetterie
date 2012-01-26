--
-- Structure de la table `stock_purchase`
--

CREATE TABLE IF NOT EXISTS `stock_purchase` (
  `id_purchase` int(11) NOT NULL,
  `userName` varchar(255) NOT NULL,
  `quantity` int(11) NOT NULL,
  `offer_id` int(11) NOT NULL,
  PRIMARY KEY (`id_purchase`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Structure de la table `stock_purchase_attribute`
--

CREATE TABLE IF NOT EXISTS `stock_purchase_attribute` (
  `Purchase_id_purchase` int(11) NOT NULL,
  `attribute_key` varchar(255) NOT NULL,
  `attribute_value` text NOT NULL,
  PRIMARY KEY (`Purchase_id_purchase`,`attribute_key`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Structure de la table `stock_purchase_attribute_date`
--

CREATE TABLE IF NOT EXISTS `stock_purchase_attribute_date` (
  `Purchase_id_purchase` int(11) NOT NULL,
  `attribute_key` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `attribute_value` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Purchase_id_purchase`,`attribute_key`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Structure de la table `stock_purchase_attribute_num`
--

CREATE TABLE IF NOT EXISTS `stock_purchase_attribute_num` (
  `Purchase_id_purchase` int(11) NOT NULL,
  `attribute_key` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `attribute_value` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`Purchase_id_purchase`,`attribute_key`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;