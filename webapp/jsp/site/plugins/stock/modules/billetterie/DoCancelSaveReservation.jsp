<jsp:useBean id="stockBilletterieReservationApp" scope="request" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.StockBilletterieReservationApp" />
<%@ page contentType="text/html; charset=UTF-8" %>

<%
String url = stockBilletterieReservationApp.doCancelSaveReservation( request, response );
if(url != null){
    response.setCharacterEncoding( "utf-8" );
	response.sendRedirect( url );
}
%>
