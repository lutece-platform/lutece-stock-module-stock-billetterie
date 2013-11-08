<%@ include file="billetterie_header.inc.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.CategoryJspBean"%>
<jsp:useBean id="category" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.CategoryJspBean" />
<% 
category.init( request, CategoryJspBean.RIGHT_MANAGE_CATEGORIES);
%>

<%=category.getSaveCategory( request, fr.paris.lutece.plugins.stock.business.category.Category.class.getName(  ) )%>
<%@ include file="../../../../AdminFooter.jsp" %>