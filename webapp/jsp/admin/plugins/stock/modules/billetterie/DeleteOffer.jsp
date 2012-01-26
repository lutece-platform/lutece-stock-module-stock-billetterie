<%@ page errorPage="../../../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<%
	OfferJspBean offer = (OfferJspBean)SpringContextService.getBean( "stock-billetterie.offerJspBean" );
	offer.init( request, OfferJspBean.RIGHT_MANAGE_OFFERS );
    response.sendRedirect( offer.getDeleteOffer( request ) );
%>

