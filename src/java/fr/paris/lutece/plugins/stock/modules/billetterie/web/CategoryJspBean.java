/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fr.paris.lutece.plugins.stock.business.category.CategoryFilter;
import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.billetterie.utils.constants.BilletterieConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowCategoryDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowCategoryService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.utils.constants.StockConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.datatable.DataTableManager;
import fr.paris.lutece.util.html.HtmlTemplate;


/**
 * The Class CategoryJspBean.
 */
public class CategoryJspBean extends AbstractJspBean
{
    public static final Logger LOGGER = Logger.getLogger( CategoryJspBean.class );

    /** The Constant PARAMETER_CATEGORY_ID. */
    public static final String PARAMETER_CATEGORY_ID = "category_id";

    /** The Constant RIGHT_MANAGE_CATEGORIES. */
    public static final String RIGHT_MANAGE_CATEGORIES = "CATEGORIES_MANAGEMENT";

    /** The Constant MARK_CATEGORY. */
    public static final String MARK_CATEGORY = "category";

    /** The constants for DataTableManager */
    public static final String MARK_DATA_TABLE_CATEGORY = "dataTableCategory";
    public static final String MARK_FILTER_CATEGORY = "filterCategory";
    public static final String MACRO_COLUMN_ACTIONS_CATEGORY = "columnActionsCategory";
    public static final String MACRO_COLUMN_NAME_CATEGORY = "columnNameCategory";

    /** The Constant MARK_TITLE. */
    public static final String MARK_TITLE = "title";

    /** The Constant PARAMETER_CATEGORY_TYPE_LIST. */
    public static final String PARAMETER_CATEGORY_TYPE_LIST = "category_type_list";

    /** The Constant PARAMETER_CATEGORY_TYPE_LIST_DEFAULT. */
    public static final String PARAMETER_CATEGORY_TYPE_LIST_DEFAULT = "category_type_list_default";

    /** The Constant PARAMETER_FILTER_NAME. */
    private static final String PARAMETER_FILTER_NAME = "filter_name";

    /** The Constant BEAN_STOCK_TICKETS_SHOW_SERVICE. */
    private static final String BEAN_STOCK_TICKETS_SHOW_SERVICE = "stock-tickets.showService";

    // I18N
    /** The Constant PAGE_TITLE_MANAGE_CATEGORY. */
    private static final String PAGE_TITLE_MANAGE_CATEGORY = "module.stock.billetterie.manage_category.title";

    /** The Constant PAGE_TITLE_CREATE_CATEGORY. */
    private static final String PAGE_TITLE_CREATE_CATEGORY = "module.stock.billetterie.create_category.title";

    /** The Constant PAGE_TITLE_MODIFY_CATEGORY. */
    private static final String PAGE_TITLE_MODIFY_CATEGORY = "module.stock.billetterie.save_category.title";

    // JSP
    /** The Constant JSP_MANAGE_CATEGORYS. */
    private static final String JSP_MANAGE_CATEGORYS = "jsp/admin/plugins/stock/modules/billetterie/ManageCategories.jsp";

    /** The Constant CATEGORY_DO_DELETE_JSP. */
    private static final String CATEGORY_DO_DELETE_JSP = "jsp/admin/plugins/stock/modules/billetterie/DoDeleteCategory.jsp";

    /** The Constant JSP_SAVE_CATEGORY. */
    private static final String JSP_SAVE_CATEGORY = "SaveCategory.jsp";

    // Templates
    /** The Constant TEMPLATE_MANAGE_CATEGORIES. */
    private static final String TEMPLATE_MANAGE_CATEGORIES = "admin/plugins/stock/modules/billetterie/manage_categories.html";

    /** The Constant TEMPLATE_SAVE_CATEGORY. */
    private static final String TEMPLATE_SAVE_CATEGORY = "admin/plugins/stock/modules/billetterie/save_category.html";

    // MESSAGES
    /** The Constant MESSAGE_CONFIRMATION_DELETE_CATEGORY. */
    private static final String MESSAGE_CONFIRMATION_DELETE_CATEGORY = "module.stock.billetterie.message.deleteCategory.confirmation";

    /** The Constant MESSAGE_DELETE_CATEGORY_WITH_SHOW. */
    private static final String MESSAGE_DELETE_CATEGORY_WITH_SHOW = "module.stock.billetterie.message.deleteCategory.with.show";

    // MEMBERS VARIABLES
    /** The _service category. */
    // @Inject
    private IShowCategoryService _serviceCategory;

    /** The _category filter. */
    private CategoryFilter _categoryFilter;

    /** The _service show. */
    // @Inject
    // @Named( "stock-tickets.showService" )
    private IShowService _serviceShow;

    /**
     * Instantiates a new category jsp bean.
     */
    public CategoryJspBean( )
    {
        // super( );
        _categoryFilter = new CategoryFilter( );
        _serviceCategory = SpringContextService.getContext( ).getBean( IShowCategoryService.class );
        _serviceShow = (IShowService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SHOW_SERVICE );
    }

    /**
     * Builds the filter.
     * 
     * @param filter the filter
     * @param request the request
     */
    protected void buildFilter( CategoryFilter filter, HttpServletRequest request )
    {
        filter.setName( request.getParameter( PARAMETER_FILTER_NAME ) );
    }

    /**
     * Generates a HTML form that displays all categorys.
     * 
     * @param request the Http request
     * @return HTML
     */
    public String getManageCategories( HttpServletRequest request )
    {
        setPageTitleProperty( PAGE_TITLE_MANAGE_CATEGORY );

        CategoryFilter filter = getCategoryFilter( request );
        List<String> orderList = new ArrayList<String>( );
        orderList.add( BilletterieConstants.NAME );
        filter.setOrders( orderList );
        filter.setOrderAsc( true );

        // Fill the model
        Map<String, Object> model = new HashMap<String, Object>( );

        //Obtention des objets sauvegardés en session
        DataTableManager<ShowCategoryDTO> dataTableToUse = getDataTable( request, filter );
        model.put( MARK_DATA_TABLE_CATEGORY, dataTableToUse );

        model.put( TicketsConstants.MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
        model.put( TicketsConstants.MARK_FILTER, filter );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_CATEGORIES, getLocale( ), model );

        //opération nécessaire pour eviter les fuites de mémoires
        dataTableToUse.clearItems( );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Get the DataTableManager object for the ShowDTO bean
     * @param request the http request
     * @param filter the filter
     * @return the data table to use
     */
    private DataTableManager<ShowCategoryDTO> getDataTable( HttpServletRequest request, CategoryFilter filter )
    {
        //si un objet est déjà présent en session, on l'utilise
        Method findMethod = null;
        try
        {
            findMethod = _serviceCategory.getClass( ).getMethod( PARAMETER_FIND_BY_FILTER_NAME_METHOD,
                    CategoryFilter.class, PaginationProperties.class );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Erreur lors de l'obtention du data table : ", e );
        }
        DataTableManager<ShowCategoryDTO> dataTableToUse = getAbstractDataTableManager( request, filter,
                MARK_DATA_TABLE_CATEGORY, JSP_MANAGE_CATEGORYS, _serviceCategory, findMethod );

        //si pas d'objet en session, il faut ajouter les colonnes à afficher
        dataTableToUse.getListColumn().clear();
        if ( dataTableToUse.getListColumn( ).isEmpty( ) )
        {
            dataTableToUse.addColumn( "module.stock.billetterie.manage_category.filter.name",
                    "name", true );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.manage_category.actionsLabel",
                    MACRO_COLUMN_ACTIONS_CATEGORY );
        }
        saveDataTableInSession( request, dataTableToUse, MARK_DATA_TABLE_CATEGORY );
        return dataTableToUse;
    }

    /**
     * Returns the form to modify a provider.
     * 
     * @param request The Http request
     * @param strCategoryClassName The class name of the provider entity to
     *            modify
     * @return the html code of the provider form
     */
    public String getSaveCategory( HttpServletRequest request, String strCategoryClassName )
    {

        ShowCategoryDTO category = null;
        Map<String, Object> model = new HashMap<String, Object>( );

        FunctionnalException fe = getErrorOnce( request );
        if ( fe != null )
        {
            category = (ShowCategoryDTO) fe.getBean( );
            model.put( BilletterieConstants.ERROR, getHtmlError( fe ) );
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
                category = new ShowCategoryDTO( );
            }
        }

        model.put( StockConstants.MARK_JSP_BACK, JSP_MANAGE_CATEGORYS );
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
     * Save a category.
     * 
     * @param request The HTTP request
     * @return redirection url
     */
    public String doSaveCategory( HttpServletRequest request )
    {
        if ( null != request.getParameter( StockConstants.PARAMETER_BUTTON_CANCEL ) )
        {
            return doGoBack( request );
        }

        ShowCategoryDTO category = new ShowCategoryDTO( );
        populate( category, request );

        try
        {
            // Controls mandatory fields
            validateBilletterie( category );
            _serviceCategory.doSaveCategory( category );
        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, JSP_SAVE_CATEGORY );

        }

        return doGoBack( request );
    }

    /**
     * Return the url of the JSP which called the last action.
     * 
     * @param request The Http request
     * @return The url of the last JSP
     */
    private String doGoBack( HttpServletRequest request )
    {
        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

        return StringUtils.isNotBlank( strJspBack ) ? ( AppPathService.getBaseUrl( request ) + strJspBack )
                : AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORYS;
    }

    /**
     * Gets the category filter.
     * 
     * @param request the request
     * @return the category filter
     */
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
     * Returns the confirmation message to delete a category.
     * 
     * @param request The Http request
     * @return the html code message
     */
    public String getDeleteCategory( HttpServletRequest request )
    {
        String strCategoryId = request.getParameter( PARAMETER_CATEGORY_ID );

        int nIdCategory;

        try
        {
            nIdCategory = Integer.parseInt( strCategoryId );
        }
        catch ( NumberFormatException e )
        {
            AppLogService.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }

        Map<String, Object> urlParam = new HashMap<String, Object>( );
        urlParam.put( PARAMETER_CATEGORY_ID, nIdCategory );

        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

        if ( StringUtils.isNotBlank( strJspBack ) )
        {
            urlParam.put( StockConstants.MARK_JSP_BACK, strJspBack );
        }

        // BO-CU02-E02-RGE01 : Aucun spectacle ne doit être rattaché à la
        // catégorie sélectionnée
        ShowFilter filter = new ShowFilter( );
        filter.setIdCategory( nIdCategory );
        ResultList<ShowDTO> bookingList = this._serviceShow.findByFilter( filter, null );

        if ( bookingList != null && !bookingList.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_DELETE_CATEGORY_WITH_SHOW,
                    AdminMessage.TYPE_STOP );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DELETE_CATEGORY,
                CATEGORY_DO_DELETE_JSP, AdminMessage.TYPE_CONFIRMATION, urlParam );
    }

    /**
     * Delete a category.
     * 
     * @param request The Http request
     * @return the html code message
     */
    public String doDeleteCategory( HttpServletRequest request )
    {
        String strCategoryId = request.getParameter( PARAMETER_CATEGORY_ID );

        int nIdCategory;

        try
        {
            nIdCategory = Integer.parseInt( strCategoryId );
        }
        catch ( NumberFormatException e )
        {
            AppLogService.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }

        _serviceCategory.doDeleteCategory( nIdCategory );

        return doGoBack( request );
    }
}
