<%@ page errorPage="../../../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.ShowJspBean"%>
<jsp:useBean id="product" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.ShowJspBean" />
<%
	product.init( request, ShowJspBean.RIGHT_MANAGE_PRODUCTS );
    response.sendRedirect( product.getDeleteProduct( request ) );
%>