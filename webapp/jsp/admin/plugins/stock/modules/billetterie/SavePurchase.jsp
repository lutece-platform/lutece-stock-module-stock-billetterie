<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean"%>
<jsp:useBean id="purchase" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean" />
<% 
	purchase.init( request, PurchaseJspBean.RIGHT_MANAGE_PURCHASES);
	String ret = purchase.getSavePurchase( request, response ); 
%>

<% 
if (ret != null) {
%>

<%@ include file="billetterie_header.inc.jsp" %>
<%= ret %>

<%
}
%>