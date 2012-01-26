<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean"%>
<% 
	PurchaseJspBean purchase = (PurchaseJspBean)SpringContextService.getBean( "stock-billetterie.purchaseJspBean" );
purchase.init( request, PurchaseJspBean.RIGHT_MANAGE_PURCHASES);
%>
<%= purchase.getManagePurchases( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>