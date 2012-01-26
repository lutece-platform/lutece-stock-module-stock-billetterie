<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean"%>
<jsp:useBean id="stat" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean" />
<% 
	stat.init( request, StatisticJspBean.RIGHT_MANAGE_STATISTICS );
%>
<%= stat.getManageStatistics( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>