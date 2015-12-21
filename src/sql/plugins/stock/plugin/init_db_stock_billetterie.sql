--
-- Contenu de la table `stock_offer_genre`
--

INSERT INTO `stock_offer_genre` (`id_offer_genre`, `name`) VALUES
(1, 'Tarif réduit'),
(2, 'Invitation'),
(3, 'Invitation spectacle enfants');

--
-- Nouvelle sequence pour les quartiers
--
INSERT INTO `stock_sequences` (`sequence_name`, `next_val`) VALUES
('billetterie_district_id',1);
