<%@page import="fr.paris.lutece.portal.service.message.SiteMessageException"%>
<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>

<jsp:useBean id="stockBilletterieReservationApp" scope="request" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.StockBilletterieReservationApp" />


<%
try {
String url = stockBilletterieReservationApp.doSaveReservation( request, response );
if(url != null){
	response.sendRedirect( url );
}
}
catch( SiteMessageException lme )
{
	response.sendRedirect( AppPathService.getBaseUrl( request ) + "jsp/site/Portal.jsp" );
}
%>
