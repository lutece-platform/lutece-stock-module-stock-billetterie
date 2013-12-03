
--
-- Contenu de la table `billetterie_portlet`
--

INSERT INTO `billetterie_portlet` (`id_portlet`, `number_show`, `type_content_portlet`) VALUES
(98, 8, 'a-laffiche'),
(99, 5, 'a-venir');

--
-- Nouvelle sequence pour les quartiers
--
INSERT INTO `stock_sequences` (`sequence_name`, `next_val`) VALUES 
('billetterie_district_id',1);