<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.ShowJspBean"%>
<jsp:useBean id="product" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.ShowJspBean" />
<% 
	product.init( request, ShowJspBean.RIGHT_MANAGE_PRODUCTS);
%>
<%= product.getManageProducts( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>