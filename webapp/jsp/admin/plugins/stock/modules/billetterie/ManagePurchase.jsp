<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean"%>
<jsp:useBean id="purchase" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.PurchaseJspBean" />
<% 
purchase.init( request, PurchaseJspBean.RIGHT_MANAGE_PURCHASES);
%>
<%= purchase.getManagePurchases( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>