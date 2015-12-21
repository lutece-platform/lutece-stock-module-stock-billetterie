<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.ShowJspBean"%>
<jsp:useBean id="product" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.ShowJspBean" />

<%
	product.init( request, ShowJspBean.RIGHT_MANAGE_PRODUCTS);
	if (request.getParameter( ShowJspBean.PARAMETER_REFRESH_CONTACT ) != null ){%>
		<%@ include file="billetterie_header.inc.jsp" %>
		<%= product.getSaveProduct( request, fr.paris.lutece.plugins.stock.business.product.Product.class.getName(  ) ) %>  
	<%}
	else {
    	response.sendRedirect( product.doSaveProduct( request ) );
	}
%>