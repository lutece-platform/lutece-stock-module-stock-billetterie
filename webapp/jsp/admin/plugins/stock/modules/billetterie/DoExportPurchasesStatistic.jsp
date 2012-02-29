<%@ page errorPage="../../../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean"%>
<jsp:useBean id="statisticPurchases" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean" />
<% 
	statisticPurchases.init( request, StatisticJspBean.RIGHT_MANAGE_STATISTICS);
	statisticPurchases.doExportStatistics( request, response );
%>