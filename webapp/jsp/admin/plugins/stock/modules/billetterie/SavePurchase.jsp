<%@ include file="billetterie_header.inc.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean"%>
<jsp:useBean id="purchase" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean" />
<% 
	purchase.init( request, PurchaseJspBean.RIGHT_MANAGE_PURCHASES);
%>

<%= purchase.getSavePurchase( request ) %>