<%@ page errorPage="../../../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean"%>
<% 
	StatisticJspBean statisticPurchases = (StatisticJspBean) SpringContextService.getBean( "stock-billetterie.statisticJspBean" );
	statisticPurchases.init( request, StatisticJspBean.RIGHT_MANAGE_STATISTICS);
	statisticPurchases.doExportStatistics( request, response );
%>