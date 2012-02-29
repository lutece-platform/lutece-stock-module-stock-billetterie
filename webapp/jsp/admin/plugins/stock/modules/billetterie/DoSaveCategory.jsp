<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.CategoryJspBean"%>
<jsp:useBean id="category" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.CategoryJspBean" />

<%
	category.init( request, CategoryJspBean.RIGHT_MANAGE_CATEGORIES);
    response.sendRedirect( category.doSaveCategory( request ) );
%>

