<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.ShowJspBean"%>
<% 
	ShowJspBean product = (ShowJspBean)SpringContextService.getBean( "stock-billetterie.productJspBean" );
	product.init( request, ShowJspBean.RIGHT_MANAGE_PRODUCTS);
%>
<%= product.getManageProducts( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>