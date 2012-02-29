<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.NotificationJspBean"%>

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.NotificationJspBean"%>
<jsp:useBean id="notification" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.NotificationJspBean" />
<% 
	notification.init( request, NotificationJspBean.RIGHT_MANAGE_NOTIFICATIONS);
    response.sendRedirect( notification.doSendNotification( request ) );
%>

