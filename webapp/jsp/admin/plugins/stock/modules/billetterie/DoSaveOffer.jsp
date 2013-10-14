<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<jsp:useBean id="offer" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean" />
<% 
	offer.init( request, OfferJspBean.RIGHT_MANAGE_OFFERS);
	if(request.getParameter(OfferJspBean.PARAMETER_REFRESH_CONTACT)!=null){%>
		<%@ include file="billetterie_header.inc.jsp" %>
		<%= offer.getSaveOffer( request ) %>  
	<%}
	else{
	    response.sendRedirect( offer.doSaveOffer( request ) );
	}
%>

