<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean"%>
<jsp:useBean id="statisticPurchases" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean" />
<% 
	statisticPurchases.init( request, StatisticJspBean.RIGHT_MANAGE_STATISTICS );
%>
<%= statisticPurchases.getManagePurchases( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>