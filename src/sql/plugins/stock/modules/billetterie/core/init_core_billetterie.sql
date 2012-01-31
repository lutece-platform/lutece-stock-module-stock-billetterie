--
-- Contenu de la table `core_feature_group`
--
UPDATE core_feature_group SET feature_group_order = feature_group_order + 1;

INSERT INTO `core_feature_group` (`id_feature_group`, `feature_group_description`, `feature_group_label`, `feature_group_order`) VALUES
('BILLETTERIE', 'module.stock.billetterie.features.group.billetterie.description', 'module.stock.billetterie.features.group.billetterie.label', 1);

--	
-- core_admin_right
--
INSERT INTO core_admin_right(id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url,id_order)
VALUES 
('STATISTICS_MANAGEMENT', 'module.stock.billetterie.adminFeature.statisticsManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManageStatistics.jsp', 'module.stock.billetterie.adminFeature.statisticsManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/actions/statistiques.png', NULL, 6),
('PARTNERS_MANAGEMENT', 'module.stock.billetterie.adminFeature.partnersManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManagePartners.jsp', 'module.stock.billetterie.adminFeature.partnersManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/plugins/stock/modules/billetterie/partner.png', '', 1),
('OFFERS_MANAGEMENT', 'module.stock.billetterie.adminFeature.offresManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManageOffers.jsp', 'module.stock.billetterie.adminFeature.offresManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/actions/seance.png', '', 4),
('CATEGORIES_MANAGEMENT', 'module.stock.billetterie.adminFeature.categoriesManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManageCategories.jsp', 'module.stock.billetterie.adminFeature.categoriesManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/actions/category.png', '', 2),
('PRODUCTS_MANAGEMENT', 'module.stock.billetterie.adminFeature.productsManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManageProducts.jsp', 'module.stock.billetterie.adminFeature.productsManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/actions/show.png', '', 3),
('PURCHASES_MANAGEMENT', 'module.stock.billetterie.adminFeature.purchasesManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManagePurchase.jsp', 'module.stock.billetterie.adminFeature.purchasesManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/plugins/stock/modules/billetterie/ticket.png', '', 5);


--
-- Contenu de la table `core_dashboard`
--

INSERT INTO `core_dashboard` (`dashboard_name`, `dashboard_column`, `dashboard_order`) VALUES
('billeterieAdminDashboardComponent', 2, 1);

--
-- Contenu de la table `core_page`
--

DELETE FROM core_page WHERE id_page = 1;

INSERT INTO `core_page` (`id_page`, `id_parent`, `name`, `description`, `date_update`, `status`, `page_order`, `id_template`, `date_creation`, `role`, `code_theme`, `node_status`, `image_content`, `mime_type`, `meta_keywords`, `meta_description`, `id_authorization_node`) VALUES
(1, 0, 'accueil', 'Page d''accueil', '2012-01-10 11:03:53', 1, 1, 4, '2003-09-09 04:38:01', 'none', 'default', 0, 0x4749463839610b001400a10100b6b6b6ffffffffc885cccccc2c000000000b001400000224448e3996ba8d86644d4e68afcb34644b7de0218e25783e2753865ff756a3fc6035d63505003b, 'image/gif', NULL, NULL, 1);


--
-- Contenu de la table `core_portlet`
--
INSERT INTO `core_portlet` (`id_portlet`, `id_portlet_type`, `id_page`, `name`, `date_update`, `status`, `portlet_order`, `column_no`, `id_style`, `accept_alias`, `date_creation`, `display_portlet_title`, `role`) VALUES
(98, 'BILLETTERIE_PORTLET', 1, 'Nos spectacles à l''affiche', '2012-01-18 14:29:40', 0, 1, 1, 1, 0, '2012-01-13 16:11:24', 0, 'none'),
(99, 'BILLETTERIE_PORTLET', 1, 'Nos spectacles à venir', '2012-01-18 09:58:47', 0, 1, 1, 1, 0, '2012-01-17 14:34:27', 0, 'none');


--
-- Contenu de la table `core_portlet_type`
--

INSERT INTO `core_portlet_type` (`id_portlet_type`, `name`, `url_creation`, `url_update`, `home_class`, `plugin_name`, `url_docreate`, `create_script`, `create_specific`, `create_specific_form`, `url_domodify`, `modify_script`, `modify_specific`, `modify_specific_form`) VALUES
('BILLETTERIE_PORTLET', 'module.stock.billetterie.portlet.name', 'plugins/stock/modules/billetterie/CreatePortletBilletterie.jsp', 'plugins/stock/modules/billetterie/ModifyPortletBilletterie.jsp', 'fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet.BilletteriePortletHome', 'stock-billetterie', 'plugins/stock/modules/billetterie/DoCreatePortletBilletterie.jsp', '/admin/portlet/script_create_portlet.html', '/admin/plugins/stock/modules/billetterie/portlet_billetterie.html', '', 'plugins/stock/modules/billetterie/DoModifyPortletBilletterie.jsp', '/admin/portlet/script_modify_portlet.html', '/admin/plugins/stock/modules/billetterie/portlet_billetterie.html', '');


--
-- Contenu de la table `core_style`
--

INSERT INTO `core_style` (`id_style`, `description_style`, `id_portlet_type`, `id_portal_component`) VALUES
(1, 'Portlet_Show', 'BILLETTERIE_PORTLET', 0);


--
-- Contenu de la table `core_stylesheet`
--

INSERT INTO `core_stylesheet` (`id_stylesheet`, `description`, `file_name`, `source`) VALUES
(1501, 'Portlet_Show_style', 'portlet_billetterie.xsl', 0x3c3f786d6c2076657273696f6e3d22312e30223f3e0d0a3c78736c3a7374796c6573686565742076657273696f6e3d22312e302220786d6c6e733a78736c3d22687474703a2f2f7777772e77332e6f72672f313939392f58534c2f5472616e73666f726d223e0d0a3c78736c3a6f7574707574206d6574686f643d2268746d6c2220696e64656e743d2279657322202f3e0d0a0d0a093c78736c3a74656d706c617465206d617463683d22706f72746c6574223e0d0a09093c6469763e0d0a0909093c78736c3a617474726962757465206e616d653d22636c617373223e706f72746c6574203c78736c3a76616c75652d6f662073656c6563743d2262696c6c657474657269652f74797065506f72746c6574222f3e3c2f78736c3a6174747269627574653e0d0a0909093c68333e0d0a090909093c78736c3a76616c75652d6f662064697361626c652d6f75747075742d6573636170696e673d22796573222073656c6563743d22706f72746c65742d6e616d6522202f3e0d0a0909093c2f68333e0d0a0909093c64697620636c6173733d22706f72746c65742d636f6e74656e74223e0d0a090909093c64697620636c6173733d2273686f775f6c6973745f70616765223e0d0a09090909093c78736c3a617474726962757465206e616d653d2268726566223e3c78736c3a76616c75652d6f662073656c6563743d2275726c222f3e3c2f78736c3a6174747269627574653e0d0a09090909093c78736c3a63686f6f73653e0d0a09090909093c78736c3a7768656e20746573743d2262696c6c657474657269655b73686f775d223e0d0a09090909093c78736c3a666f722d656163682073656c6563743d2262696c6c657474657269652f73686f77223e0d0a0d0a0909090909093c64697620636c6173733d2273686f775f64657461696c73223e0d0a0d0a090909090909093c613e0d0a09090909090909093c78736c3a617474726962757465206e616d653d227469746c65223e3c78736c3a76616c75652d6f662073656c6563743d226e616d65222f3e3c2f78736c3a6174747269627574653e0d0a09090909090909093c78736c3a617474726962757465206e616d653d2268726566223e3c78736c3a76616c75652d6f662073656c6563743d2275726c222f3e3c2f78736c3a6174747269627574653e0d0a09090909090909093c696d673e0d0a0909090909090909093c78736c3a617474726962757465206e616d653d22737263223e3c215b43444154415b696d616765732f706f737465722f74625f5d5d3e3c78736c3a76616c75652d6f662073656c6563743d22706f737465724e616d65222f3e3c2f78736c3a6174747269627574653e0d0a0909090909090909093c78736c3a617474726962757465206e616d653d22616c74223e3c78736c3a76616c75652d6f662073656c6563743d226e616d65222f3e3c2f78736c3a6174747269627574653e0d0a09090909090909093c2f696d673e0d0a090909090909093c2f613e0d0a090909090909093c62722f3e0d0a090909090909093c6120636c6173733d22656e2d7361766f69722d706c7573223e0d0a09090909090909093c78736c3a617474726962757465206e616d653d2268726566223e3c215b43444154415b6a73702f736974652f506f7274616c2e6a73703f706167653d62696c6c6574746572696526616374696f6e3d66696368652d737065637461636c652670726f647563745f69643d5d5d3e3c78736c3a76616c75652d6f662073656c6563743d226964222f3e3c2f78736c3a6174747269627574653e0d0a09090909090909093c78736c3a617474726962757465206e616d653d227469746c65223e44c3a97461696c7320647520737065637461636c653c2f78736c3a6174747269627574653e0d0a09090909090909093c78736c3a76616c75652d6f662073656c6563743d2263617465676f72794e616d6522202f3e0d0a09090909090909093c62722f3e0d0a0909090909090909454e205341564f495209504c55530d0a090909090909093c2f613e0d0a0909090909093c2f6469763e0d0a09090909093c2f78736c3a666f722d656163683e0d0a09090909090d0a09090909093c6272207374796c653d22636c6561723a626f746822202f3e0d0a09090909093c7370616e20636c6173733d226c69656e2d64657461696c223e0d0a09090909093c613e0d0a09090909093c78736c3a63686f6f73653e0d0a0909090909093c78736c3a7768656e20746573743d2262696c6c657474657269652f74797065506f72746c6574203d2027614c6166666963686527223e0d0a090909090909093c78736c3a617474726962757465206e616d653d2268726566223e6a73702f736974652f506f7274616c2e6a73703f706167653d62696c6c6574746572696526616d703b616374696f6e3d612d6c616666696368653c2f78736c3a6174747269627574653e0d0a09090909090909566f697220746f7573206c657320737065637461636c657320656e20636f7572730d0a0909090909093c2f78736c3a7768656e3e0d0a0909090909093c78736c3a6f74686572776973653e0d0a090909090909093c78736c3a617474726962757465206e616d653d2268726566223e6a73702f736974652f506f7274616c2e6a73703f706167653d62696c6c6574746572696526616d703b616374696f6e3d612d76656e69723c2f78736c3a6174747269627574653e0d0a09090909090909566f697220746f7573206c657320737065637461636c657320c3a02076656e69720d0a0909090909093c2f78736c3a6f74686572776973653e0d0a09090909093c2f78736c3a63686f6f73653e0d0a09090909093c2f613e0d0a09090909093c2f7370616e3e0d0a09090909093c2f78736c3a7768656e3e0d0a09090909093c78736c3a6f74686572776973653e0d0a09090909093c78736c3a63686f6f73653e0d0a0909090909093c78736c3a7768656e20746573743d2262696c6c657474657269652f74797065506f72746c6574203d2027614c6166666963686527223e0d0a09090909090950617320646520737065637461636c6520656e20636f7572732e0d0a0909090909093c2f78736c3a7768656e3e0d0a0909090909093c78736c3a6f74686572776973653e0d0a09090909090950617320646520737065637461636c6520c3a02076656e69722e0d0a0909090909093c2f78736c3a6f74686572776973653e0d0a09090909093c2f78736c3a63686f6f73653e0d0a09090909093c2f78736c3a6f74686572776973653e0d0a090909093c2f78736c3a63686f6f73653e0d0a090909093c2f6469763e0d0a0909093c2f6469763e0d0a09093c2f6469763e0d0a093c2f78736c3a74656d706c6174653e0d0a0d0a3c2f78736c3a7374796c6573686565743e0d0a);


--
-- Contenu de la table `core_style_mode_stylesheet`
--

INSERT INTO `core_style_mode_stylesheet` (`id_style`, `id_mode`, `id_stylesheet`) VALUES
(1, 0, 1501);


--
-- Contenu de la table `core_user_right`
--

INSERT INTO `core_user_right` (`id_right`, `id_user`) VALUES
('CATEGORIES_MANAGEMENT', 1),
('CATEGORIES_MANAGEMENT', 2),
('OFFERS_MANAGEMENT', 1),
('PARTNERS_MANAGEMENT', 1),
('PRODUCTS_MANAGEMENT', 1),
('PRODUCTS_MANAGEMENT', 2),
('PURCHASES_MANAGEMENT', 1),
('STATISTICS_MANAGEMENT', 1);


