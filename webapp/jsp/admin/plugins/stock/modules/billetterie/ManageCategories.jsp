<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.CategoryJspBean"%>
<jsp:useBean id="category" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.CategoryJspBean" />
<% 
	category.init( request, CategoryJspBean.RIGHT_MANAGE_CATEGORIES);
%>
<%= category.getManageCategories( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>