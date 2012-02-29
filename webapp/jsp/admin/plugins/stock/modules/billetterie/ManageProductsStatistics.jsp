<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean"%>
<jsp:useBean id="statisticProducts" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.StatisticJspBean" />
<% 
	statisticProducts.init( request, StatisticJspBean.RIGHT_MANAGE_STATISTICS );
%>
<%= statisticProducts.getManageProducts( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>