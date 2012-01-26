<%@ include file="billetterie_header.inc.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean"%>
<% 
	PurchaseJspBean purchase = (PurchaseJspBean)SpringContextService.getBean( "stock-billetterie.purchaseJspBean" );
	purchase.init( request, PurchaseJspBean.RIGHT_MANAGE_PURCHASES);
%>

<%= purchase.getSavePurchase( request ) %>