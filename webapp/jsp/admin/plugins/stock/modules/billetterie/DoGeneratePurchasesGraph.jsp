<%@ page errorPage="../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean"%>

<jsp:useBean id="statisticPurchase" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean" />
<% 
	statisticPurchase.init( request, StatisticJspBean.RIGHT_MANAGE_STATISTICS);
	statisticPurchase.doGenerateGraph( request , response ); 
%>
