<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean"%>
<jsp:useBean id="stat" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean" />

<%
	stat.init( request, StatisticJspBean.RIGHT_MANAGE_STATISTICS);
    response.sendRedirect( stat.doEditSurvey( request ) );
%>

