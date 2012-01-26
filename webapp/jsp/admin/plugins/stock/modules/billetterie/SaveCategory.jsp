<%@ include file="billetterie_header.inc.jsp" %>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.CategoryJspBean"%>
<% 
CategoryJspBean category = (CategoryJspBean)SpringContextService.getBean( "stock-billetterie.categoryJspBean" );
category.init( request, CategoryJspBean.RIGHT_MANAGE_CATEGORIES);
%>

<%=category.getSaveCategory( request, fr.paris.lutece.plugins.stock.business.category.Category.class.getName(  ) )%>