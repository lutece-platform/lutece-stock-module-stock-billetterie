<%@ include file="billetterie_header.inc.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.NotificationJspBean"%>
<% 
	NotificationJspBean notification = (NotificationJspBean)SpringContextService.getBean( "stock-billetterie.notificationJspBean" );
	notification.init( request, NotificationJspBean.RIGHT_MANAGE_NOTIFICATIONS );
%>

<%= notification.getSendNotification( request ) %>