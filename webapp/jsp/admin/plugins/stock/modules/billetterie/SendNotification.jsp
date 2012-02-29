<%@ include file="billetterie_header.inc.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.NotificationJspBean"%>
<jsp:useBean id="notification" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.NotificationJspBean" />

<% 
	notification.init( request, NotificationJspBean.RIGHT_MANAGE_NOTIFICATIONS );
%>

<%= notification.getSendNotification( request ) %>