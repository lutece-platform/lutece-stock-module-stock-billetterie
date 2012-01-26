/*
 * Copyright (c) 2002-2008, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.stock.modules.billetterie.web;

import fr.paris.lutece.plugins.stock.business.category.Category;
import fr.paris.lutece.plugins.stock.business.category.CategoryFilter;
import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ICategoryService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.utils.constants.StockConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.html.DelegatePaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class CategoryJspBean extends AbstractJspBean {

	public static final String PARAMETER_CATEGORY_ID = "category_id";

	public static final String RIGHT_MANAGE_CATEGORIES = "CATEGORIES_MANAGEMENT";
	public static final String MARK_CATEGORY = "category";
    public static final String MARK_TITLE = "title";
	public static final String PARAMETER_CATEGORY_TYPE_LIST = "category_type_list";
	public static final String PARAMETER_CATEGORY_TYPE_LIST_DEFAULT = "category_type_list_default";
	private static final String MARK_LIST_CATEGORIES = "list_categories";
	private static final String PARAMETER_FILTER_NAME = "filter_name";

	// I18N
	private static final String PAGE_TITLE_MANAGE_CATEGORY = "module.stock.billetterie.manage_category.title";
    private static final String PAGE_TITLE_CREATE_CATEGORY = "module.stock.billetterie.create_category.title";
	private static final String PAGE_TITLE_MODIFY_CATEGORY = "module.stock.billetterie.save_category.title";

	// JSP
	private static final String JSP_MANAGE_CATEGORYS = "jsp/admin/plugins/stock/modules/billetterie/ManageCategories.jsp";
	private static final String CATEGORY_DO_DELETE_JSP = "jsp/admin/plugins/stock/modules/billetterie/DoDeleteCategory.jsp";

	// Templates
	private static final String TEMPLATE_MANAGE_CATEGORIES = "admin/plugins/stock/modules/billetterie/manage_categories.html";
    private static final String TEMPLATE_SAVE_CATEGORY = "admin/plugins/stock/modules/billetterie/save_category.html";

	// MESSAGES
	private static final String MESSAGE_CONFIRMATION_DELETE_CATEGORY = "module.stock.billetterie.message.deleteCategory.confirmation";
    private static final String MESSAGE_DELETE_CATEGORY_WITH_SHOW = "module.stock.billetterie.message.deleteCategory.with.show";

	// Variables
	// Paginator ManageCategories
	private int _nDefaultItemsPerPage;
	private String _strCurrentPageIndex;
	private int _nItemsPerPage;

	// MEMBERS VARIABLES
	@Inject
	private ICategoryService _serviceCategory;
	private CategoryFilter _categoryFilter;
    @Inject
    @Named( "stock-tickets.showService" )
    private IShowService _serviceShow;

	public CategoryJspBean() {
		// super( );
		_categoryFilter = new CategoryFilter();
	}

	protected void buildFilter(CategoryFilter filter, HttpServletRequest request) {
		filter.setName(request.getParameter(PARAMETER_FILTER_NAME));
	}

	/**
	 * Generates a HTML form that displays all categorys.
	 * 
	 * @param request
	 *            the Http request
	 * @return HTML
	 */
	public String getManageCategories(HttpServletRequest request) {
		setPageTitleProperty(PAGE_TITLE_MANAGE_CATEGORY);

		_strCurrentPageIndex = Paginator.getPageIndex(request,
				Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex);
		_nDefaultItemsPerPage = AppPropertiesService.getPropertyInt(
				TicketsConstants.PROPERTY_DEFAULT_ITEM_PER_PAGE, 50);
		_nItemsPerPage = Paginator.getItemsPerPage(request,
				Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
				_nDefaultItemsPerPage);

		CategoryFilter filter = getCategoryFilter(request);
        List<String> orderList = new ArrayList<String>( );
        orderList.add( "name" );
        filter.setOrders( orderList );
        filter.setOrderAsc( true );

		ResultList<Category> listAllCATEGORY = _serviceCategory
                .findByFilter( filter, getPaginationProperties( request ) );

		DelegatePaginator<Category> paginator = getPaginator(request,
				listAllCATEGORY);

		// Fill the model
		Map<String, Object> model = new HashMap<String, Object>();

		model.put(TicketsConstants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage);
		model.put(TicketsConstants.MARK_PAGINATOR, paginator);
		model.put(MARK_LIST_CATEGORIES, paginator.getPageItems());
		// the filter
		model.put(TicketsConstants.MARK_FILTER, filter);

		HtmlTemplate template = AppTemplateService.getTemplate(
				TEMPLATE_MANAGE_CATEGORIES, getLocale(), model);

		return getAdminPage(template.getHtml());
	}

	/**
	 * Returns the form to modify a provider
	 * 
	 * @param request
	 *            The Http request
	 * @param strCategoryClassName
	 *            The class name of the provider entity to modify
	 * @return the html code of the provider form
	 */
    public String getSaveCategory( HttpServletRequest request, String strCategoryClassName )
    {

		Category category = null;
        Map<String, Object> model = new HashMap<String, Object>( );

        FunctionnalException fe = getErrorOnce( request );
        if ( fe != null )
        {
            category = (Category) fe.getBean( );
            model.put( "error", getHtmlError( fe ) );
        }
        else
        {
            String strCategoryId = request.getParameter( PARAMETER_CATEGORY_ID );
            if ( strCategoryId != null )
            {
                setPageTitleProperty( PAGE_TITLE_MODIFY_CATEGORY );
                int nIdCategory = Integer.parseInt( strCategoryId );
                category = _serviceCategory.findById( nIdCategory );
            }
            else
            {
                setPageTitleProperty( PAGE_TITLE_CREATE_CATEGORY );
                category = new Category( );
            }
        }

		model.put(StockConstants.MARK_JSP_BACK, JSP_MANAGE_CATEGORYS );
        model.put( MARK_CATEGORY, category );

        if ( category.getId( ) != null && category.getId( ) != 0 )
        {
            model.put( MARK_TITLE, I18nService.getLocalizedString( PAGE_TITLE_MODIFY_CATEGORY, Locale.getDefault( ) ) );
        }
        else
        {
            model.put( MARK_TITLE, I18nService.getLocalizedString( PAGE_TITLE_CREATE_CATEGORY, Locale.getDefault( ) ) );
        }

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SAVE_CATEGORY, getLocale( ), model );

		return getAdminPage( template.getHtml( ) );
	}

	/**
	 * Save a category
	 * 
	 * @param request The HTTP request
	 * @return redirection url
	 */
	public String doSaveCategory(HttpServletRequest request)
	{
        if ( StringUtils.isNotBlank( request.getParameter( StockConstants.PARAMETER_BUTTON_CANCEL ) ) )
        {
            return doGoBack( request );
        }

        Category category = new Category( );
        populate( category, request );

        try
        {
			// Controls mandatory fields
			validate(category);
            _serviceCategory.doSaveCategory( category );
        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, "SaveCategory.jsp" );

        }

		return doGoBack(request);
	}

	/**
	 * Return the url of the JSP which called the last action
	 * 
	 * @param request
	 *            The Http request
	 * @return The url of the last JSP
	 */
    private String doGoBack( HttpServletRequest request )
    {
        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

		return StringUtils.isNotBlank( strJspBack ) ? ( AppPathService.getBaseUrl( request ) + strJspBack )
                : AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORYS;
	}

    private CategoryFilter getCategoryFilter( HttpServletRequest request )
    {
        // SORT
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        CategoryFilter filter = new CategoryFilter( );
        buildFilter( filter, request );
        _categoryFilter = filter;

        if ( strSortedAttributeName != null )
        {
        	_categoryFilter.getOrders( ).add( strSortedAttributeName );

            String strAscSort = request.getParameter( Parameters.SORTED_ASC );
            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );
            _categoryFilter.setOrderAsc( bIsAscSort );
        }

		return _categoryFilter;
	}

	/**
	 * Returns the confirmation message to delete a category
	 * 
	 * @param request
	 *            The Http request
	 * @return the html code message
	 */
    public String getDeleteCategory( HttpServletRequest request )
    {
		String strCategoryId = request.getParameter(PARAMETER_CATEGORY_ID);

		int nIdCategory;

		try
		{
			nIdCategory = Integer.parseInt(strCategoryId);
		}
		catch (NumberFormatException e)
		{
			AppLogService.debug(e);

			return AdminMessageService.getMessageUrl(request,
					StockConstants.MESSAGE_ERROR_OCCUR, AdminMessage.TYPE_STOP);
		}

		Map<String, Object> urlParam = new HashMap<String, Object>();
		urlParam.put(PARAMETER_CATEGORY_ID, nIdCategory);

		String strJspBack = request.getParameter(StockConstants.MARK_JSP_BACK);

		if (StringUtils.isNotBlank(strJspBack)) {
			urlParam.put(StockConstants.MARK_JSP_BACK, strJspBack);
		}
		
        // BO-CU02-E02-RGE01 : Aucun spectacle ne doit être rattaché à la
        // catégorie sélectionnée
        ShowFilter filter = new ShowFilter( );
        filter.setIdCategory( nIdCategory );
        ResultList<ShowDTO> bookingList = this._serviceShow.findByFilter( filter, null );

        if ( bookingList != null && !bookingList.isEmpty( ) )
		{
        	return AdminMessageService.getMessageUrl( request, MESSAGE_DELETE_CATEGORY_WITH_SHOW, AdminMessage.TYPE_STOP );
		}

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DELETE_CATEGORY,
				CATEGORY_DO_DELETE_JSP, AdminMessage.TYPE_CONFIRMATION,
				urlParam);
	}

	/**
	 * Delete a category
	 * 
	 * @param request
	 *            The Http request
	 * @return the html code message
	 */
    public String doDeleteCategory( HttpServletRequest request )
	{
		String strCategoryId = request.getParameter(PARAMETER_CATEGORY_ID);

		int nIdCategory;

		try
		{
			nIdCategory = Integer.parseInt(strCategoryId);
		}
		catch (NumberFormatException e)
		{
			AppLogService.debug(e);

			return AdminMessageService.getMessageUrl(request,
					StockConstants.MESSAGE_ERROR_OCCUR, AdminMessage.TYPE_STOP);
		}

        _serviceCategory.doDeleteCategory( nIdCategory );
		
		return doGoBack(request);
	}
}
