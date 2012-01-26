<%@ page errorPage="../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean"%>

<% 
	StatisticJspBean statisticPurchase = (StatisticJspBean) SpringContextService.getBean( "stock-billetterie.statisticJspBean" );
	statisticPurchase.init( request, StatisticJspBean.RIGHT_MANAGE_STATISTICS);
	statisticPurchase.doGenerateGraph( request , response ); 
%>
