<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<jsp:useBean id="offer" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean" />
<% 
	offer.init( request, OfferJspBean.RIGHT_MANAGE_OFFERS);
    response.sendRedirect( offer.doSaveOffer( request ) );
%>

