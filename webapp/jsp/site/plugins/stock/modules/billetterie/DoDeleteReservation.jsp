<jsp:useBean id="stockBilletterieReservationApp" scope="request" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.StockBilletterieReservationApp" />

<%
String url = stockBilletterieReservationApp.doDeleteReservation( request, response );
if(url != null){
	response.sendRedirect( url );
}
%>
