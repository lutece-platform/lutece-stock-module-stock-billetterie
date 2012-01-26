<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.CategoryJspBean"%>

<%
	CategoryJspBean category = (CategoryJspBean)SpringContextService.getBean( "stock-billetterie.categoryJspBean" );
	category.init( request, CategoryJspBean.RIGHT_MANAGE_CATEGORIES);
    response.sendRedirect( category.doSaveCategory( request ) );
%>

