--
-- core_feature_group
--
INSERT INTO core_feature_group(id_feature_group, feature_group_description, feature_group_label, feature_group_order)
	VALUES ('BILLETTERIE', 'module.stock.billetterie.features.group.billetterie.description', 'module.stock.billetterie.features.group.billetterie.label', 8);

--	
-- core_admin_right
--
INSERT INTO core_admin_right(id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url,id_order)
VALUES 
('STATISTICS_MANAGEMENT', 'module.stock.billetterie.adminFeature.statisticsManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManageStatistics.jsp', 'module.stock.billetterie.adminFeature.statisticsManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/plugins/stock/modules/billetterie/statistics.png', '', 4),
('PARTNERS_MANAGEMENT', 'module.stock.billetterie.adminFeature.partnersManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManagePartners.jsp', 'module.stock.billetterie.adminFeature.partnersManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/plugins/stock/modules/billetterie/partner.png', '', 3),
('OFFERS_MANAGEMENT', 'module.stock.billetterie.adminFeature.offresManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManageOffers.jsp', 'module.stock.billetterie.adminFeature.offresManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/plugins/stock/modules/billetterie/ticket.png', '', 2);
('CATEGORIES_MANAGEMENT', 'module.stock.billetterie.adminFeature.categoriesManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManageCategories.jsp', 'module.stock.billetterie.adminFeature.categoriesManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/plugins/stock/modules/billetterie/ticket.png', '', 1);
('PRODUCTS_MANAGEMENT', 'module.stock.billetterie.adminFeature.productsManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManageProducts.jsp', 'module.stock.billetterie.adminFeature.productsManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/plugins/stock/modules/billetterie/ticket.png', '', 5),
('PURCHASES_MANAGEMENT', 'module.stock.billetterie.adminFeature.purchasesManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/ManagePurchase.jsp', 'module.stock.billetterie.adminFeature.purchasesManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/plugins/stock/modules/billetterie/ticket.png', '', 6),
('NOTIFICATIONS_MANAGEMENT', 'module.stock.billetterie.adminFeature.notificationsManagement.name', 2, 'jsp/admin/plugins/stock/modules/billetterie/SendNotification.jsp', 'module.stock.billetterie.adminFeature.notificationsManagement.description', 0, 'stock-billetterie', 'BILLETTERIE', 'images/admin/skin/plugins/stock/modules/billetterie/ticket.png', '', 7);