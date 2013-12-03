--
-- Structure de la table `billetterie_portlet`
--

CREATE TABLE IF NOT EXISTS `billetterie_portlet` (
  `id_portlet` int(11) NOT NULL AUTO_INCREMENT,
  `number_show` int(25) NOT NULL,
  `type_content_portlet` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id_portlet`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=100 ;


--
-- Structure de la table `billetterie_quartier`
--
CREATE TABLE IF NOT EXISTS `billetterie_district` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `libelle` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

