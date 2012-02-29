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

import fr.paris.lutece.plugins.stock.business.provider.ProviderFilter;
import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.tickets.business.PartnerDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IProviderService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.utils.constants.StockConstants;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.html.DelegatePaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * The Class PartnerJspBean.
 */
public class PartnerJspBean extends AbstractJspBean
{
    public static final String PARAMETER_PARTNER_TYPE_LIST = "partner_type_list";
    public static final String PARAMETER_PARTNER_TYPE_LIST_DEFAULT = "partner_type_list_default";
    public static final String PARAMETER_PARTNER_ID = "partner_id";
    public static final String RIGHT_MANAGE_PARTNERS = "PARTNERS_MANAGEMENT";
    public static final String MARK_PARTNER = "partner";
    protected static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    protected static final String PROPERTY_DEFAULT_SESSION_PER_PAGE = "stock.itemsPerPage";
    private static final String MARK_LIST_PARTNERS = "list_partners";

    // I18N
    private static final String PAGE_TITLE_MANAGE_PARTNER = "module.stock.billetterie.manage_partner.title";
    private static final String PAGE_TITLE_CREATE_PARTNER = "module.stock.billetterie.create_partner.title";
    private static final String PAGE_TITLE_MODIFY_PARTNER = "module.stock.billetterie.modify_partner.title";

    // JSP
    private static final String JSP_MANAGE_PARTNERS = "jsp/admin/plugins/stock/modules/billetterie/ManagePartners.jsp";

    // Templates
    private static final String TEMPLATE_MANAGE_PARTNERS = "admin/plugins/stock/modules/billetterie/manage_partners.html";
    private static final String TEMPLATE_SAVE_PARTNER = "admin/plugins/stock/modules/billetterie/save_partner.html";

    //MESSAGES
    private static final String MESSAGE_CONFIRMATION_DELETE_PARTNER = "module.stock.billetterie.message.deletePartner.confirmation";
    private static final String MESSAGE_DELETE_PARTNER_WITH_SHOW = "module.stock.billetterie.message.deletePartner.with.show";


    // Variables
    // Paginator ManagePartners
    private int _nItemsPerPage;

    // MEMBERS VARIABLES
    // @Inject
    private IProviderService _serviceProvider;
    private ProviderFilter _providerFilter;
    // @Inject
    // @Named( "stock-tickets.showService" )
    private IShowService _serviceShow;

    /**
     * Instantiates a new partner jsp bean.
     */
    public PartnerJspBean( )
    {
        super(  );
        _providerFilter = new ProviderFilter( );
        _serviceProvider = SpringContextService.getContext( ).getBean( IProviderService.class );
        _serviceShow = (IShowService) SpringContextService.getBean( "stock-tickets.showService" );
    }

    /**
     * Builds the filter.
     * 
     * @param filter the filter
     * @param request the request
     */
    protected void buildFilter( ProviderFilter filter, HttpServletRequest request )
    {
        populate( filter, request );
    }

    /**
     * Gets the provider filter.
     * 
     * @param request the request
     * @return the provider filter
     */
    private ProviderFilter getProviderFilter( HttpServletRequest request )
    {
        // SORT
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        // "filter" in request  ==> new filter. use old one otherwise
        ProviderFilter filter = new ProviderFilter( );
        buildFilter( filter, request );
        _providerFilter = filter;

        if ( strSortedAttributeName != null )
        {
        	_providerFilter.getOrders( ).add( strSortedAttributeName );

            String strAscSort = request.getParameter( Parameters.SORTED_ASC );
            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );
            _providerFilter.setOrderAsc( bIsAscSort );
        }

        return _providerFilter;
    }

    /**
    * Generates a HTML form that displays all partners.
    * @param request the Http request
    * @return HTML
    */
    public String getManageProviders( HttpServletRequest request )
    {
        setPageTitleProperty( PAGE_TITLE_MANAGE_PARTNER );

        ProviderFilter filter = getProviderFilter( request );
        List<String> orderList = new ArrayList<String>( );
        orderList.add( "name" );
        filter.setOrders( orderList );
        filter.setOrderAsc( true );

        ResultList<PartnerDTO> listAllPartner = _serviceProvider
                .findByFilter( filter, getPaginationProperties( request ) );

        DelegatePaginator<PartnerDTO> paginator = getPaginator( request, listAllPartner );

        // Fill the model
        Map<String, Object> model = new HashMap<String, Object>(  );

        // the paginator
        model.put( TicketsConstants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( TicketsConstants.MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_PARTNERS, paginator.getPageItems(  ) );
        // the filter
        model.put( TicketsConstants.MARK_FILTER, filter );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PARTNERS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the form for partner creation and modification
     * @param request The HTTP request
     * @return HTML Form
     */
    public String getSavePartner( HttpServletRequest request )
    {
        PartnerDTO provider = null;
        Map<String, Object> model = new HashMap<String, Object>( );

        // Manage validation errors
        FunctionnalException ve = getErrorOnce( request );
        if ( ve != null )
        {
            provider = (PartnerDTO) ve.getBean( );
            model.put( "error", getHtmlError( ve ) );
        }
        else
        {
            // No error, get provider if modify
            String strProviderId = request.getParameter( PARAMETER_PARTNER_ID );
            if ( strProviderId != null )
            {
                setPageTitleProperty( PAGE_TITLE_MODIFY_PARTNER );
                int nIdProvider = Integer.parseInt( strProviderId );
                provider = _serviceProvider.findByIdWithProducts( nIdProvider );
            }
            else
            {
                setPageTitleProperty( PAGE_TITLE_CREATE_PARTNER );
                // Create new Partner
                provider = new PartnerDTO( );
            }

        }



        // Add the JSP wich called this action
        model.put( StockConstants.MARK_JSP_BACK, request.getParameter( StockConstants.MARK_JSP_BACK ) );
        model.put( MARK_PARTNER, provider );


        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SAVE_PARTNER, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }


    /**
     * Save a partner
     * @param request The HTTP request
     * @return redirection url
     */
    public String doSavePartner( HttpServletRequest request )
    {
        if ( StringUtils.isNotBlank( request.getParameter( "cancel" ) ) )
        {
            return doGoBack( request );
        }

        PartnerDTO provider = new PartnerDTO( );
        populate( provider, request );

        try
        {
            // Controls mandatory fields
            validate( provider );
            _serviceProvider.doSaveProvider( provider );
        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, "SavePartner.jsp" );
        }

        return doGoBack( request );
    }

    /**
     * Returns the confirmation message to delete a partner
     *
     * @param request The Http request
     * @return the html code message
     */
    public String getDeletePartner( HttpServletRequest request )
    {
        String strProviderId = request.getParameter( PARAMETER_PARTNER_ID );

        int nIdProvider;

        try
        {
            nIdProvider = Integer.parseInt( strProviderId );
        }
        catch ( NumberFormatException e )
        {
            AppLogService.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( "jsp/admin/plugins/stock/modules/billetterie/DoDeletePartner.jsp" );

        Map<String, Object> urlParam = new HashMap<String, Object>( );
        urlParam.put( PARAMETER_PARTNER_ID, nIdProvider );

        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

        if ( StringUtils.isNotBlank( strJspBack ) )
        {
            urlParam.put( StockConstants.MARK_JSP_BACK, strJspBack );
        }

        // BO-CU01-E02-RGE01 : Aucun spectacle ne doit être rattaché à la salle
        // sélectionnée
        ShowFilter filter = new ShowFilter( );
        filter.setIdProvider( nIdProvider );
        ResultList<ShowDTO> bookingList = this._serviceShow.findByFilter( filter, null );

        if ( bookingList != null && !bookingList.isEmpty( ) )
		{
        	return AdminMessageService.getMessageUrl( request, MESSAGE_DELETE_PARTNER_WITH_SHOW, AdminMessage.TYPE_STOP );
		}
        
        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DELETE_PARTNER, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION, urlParam );
    }

    /**
     * Delete a partner
     *
     * @param request The Http request
     * @return the html code message
     */
    public String doDeletePartner( HttpServletRequest request )
    {

        // if ( !GlobalProviderService.getInstance( ).isAuthorizedToDelete(
        // getUser( ), strProviderClassName ) )
        // {
        // return getManageProviders( request );
        // }

        String strProviderId = request.getParameter( PARAMETER_PARTNER_ID );

        int nIdProvider;

        try
        {
            nIdProvider = Integer.parseInt( strProviderId );
        }
        catch ( NumberFormatException e )
        {
            AppLogService.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }

        _serviceProvider.doDeleteProvider( nIdProvider );

        return doGoBack( request );
    }

    /**
     * Return the url of the JSP which called the last action
     * @param request The Http request
     * @return The url of the last JSP
     */
    private String doGoBack( HttpServletRequest request )
    {
        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

        return StringUtils.isNotBlank( strJspBack ) ? ( AppPathService.getBaseUrl( request ) + strJspBack )
                : AppPathService.getBaseUrl( request ) + JSP_MANAGE_PARTNERS;
    }
}
