<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean"%>
<jsp:useBean id="purchase" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean" />
<% 
	purchase.init( request, PurchaseJspBean.RIGHT_MANAGE_PURCHASES);
	
	if(request.getParameter( "save" ) == null && request.getParameter( "cancel" ) == null){%>
	<%@ include file="billetterie_header.inc.jsp" %>
	<%= purchase.getSavePurchase( request, response ) %>
		<%@ include file="../../../../AdminFooter.jsp" %>
	<%}
	else{
		response.sendRedirect( purchase.doSavePurchase( request ) ); 
	}
%>
