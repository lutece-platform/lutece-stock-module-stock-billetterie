<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean"%>
<jsp:useBean id="purchase" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean" />
<% 
	purchase.init( request, PurchaseJspBean.RIGHT_MANAGE_PURCHASES);
	
	if( request.getParameter( "masseDelete" ) != null )
	{
	    response.sendRedirect( purchase.getMasseDeletePurchase( request ) );
	}
	else
	{
		%>
			<jsp:include page="../../../../AdminHeader.jsp" />
			<%= purchase.getManagePurchases( request ) %>
			<%@ include file="../../../../AdminFooter.jsp" %>
		<%  
	}
%>
