/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fr.paris.lutece.plugins.stock.business.provider.ProviderFilter;
import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.billetterie.business.district.District;
import fr.paris.lutece.plugins.stock.modules.billetterie.business.district.DistrictFilter;
import fr.paris.lutece.plugins.stock.modules.billetterie.service.district.DistrictService;
import fr.paris.lutece.plugins.stock.modules.billetterie.utils.constants.BilletterieConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.business.Contact;
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
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.beanvalidation.ValidationError;
import fr.paris.lutece.util.datatable.DataTableManager;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;


/**
 * The Class PartnerJspBean.
 */
public class PartnerJspBean extends AbstractJspBean
{
    public static final Logger LOGGER = Logger.getLogger( PartnerJspBean.class );

    public static final String PARAMETER_PARTNER_TYPE_LIST = "partner_type_list";
    public static final String PARAMETER_PARTNER_TYPE_LIST_DEFAULT = "partner_type_list_default";
    public static final String PARAMETER_PARTNER_ID = "partner_id";
    public static final String PARAMETER_CONTACT_ID = "contact_id";
    public static final String PARAMETER_ADD_CONTACT = "action_add_contact";
    public static final String PARAMETER_DEL_CONTACT = "action_del_contact";
    public static final String PARAMETER_NAME = "name";

    public static final String RIGHT_MANAGE_PARTNERS = "PARTNERS_MANAGEMENT";

    public static final String MARK_PARTNER_NUMBER_CONTACT = "number_contact";
    public static final String MARK_PARTNER = "partner";
    public static final String MARK_LIST_CONTACT = "list_contacts";
    public static final String MARK_LIST_PARTNERS = "list_partners";
    public static final String MARK_LIST_DISTRICT = "list_district";

    /** The constants for DataTableManager */
    public static final String MARK_DATA_TABLE_PARTNER = "dataTablePartner";
    public static final String MARK_FILTER_PARTNER = "filterPartner";
    public static final String MACRO_COLUMN_ACTIONS_PARTNER = "columnActionsPartner";
    public static final String MACRO_COLUMN_NAME_PARTNER = "columnNamePartner";

    protected static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    protected static final String PROPERTY_DEFAULT_SESSION_PER_PAGE = "stock.itemsPerPage";

    // BEANS
    private static final String BEAN_STOCK_TICKETS_SHOW_SERVICE = "stock-tickets.showService";

    // I18N
    private static final String PAGE_TITLE_MANAGE_PARTNER = "module.stock.billetterie.manage_partner.title";
    private static final String PAGE_TITLE_CREATE_PARTNER = "module.stock.billetterie.create_partner.title";
    private static final String PAGE_TITLE_MODIFY_PARTNER = "module.stock.billetterie.modify_partner.title";

    // JSP
    private static final String JSP_DO_DELETE_PARTNER = "jsp/admin/plugins/stock/modules/billetterie/DoDeletePartner.jsp";
    private static final String JSP_MANAGE_PARTNERS = "jsp/admin/plugins/stock/modules/billetterie/ManagePartners.jsp";
    private static final String JSP_SAVE_PARTNER = "SavePartner.jsp";

    // Templates
    private static final String TEMPLATE_MANAGE_PARTNERS = "admin/plugins/stock/modules/billetterie/manage_partners.html";
    private static final String TEMPLATE_SAVE_PARTNER = "admin/plugins/stock/modules/billetterie/save_partner.html";

    //MESSAGES
    private static final String MESSAGE_CONFIRMATION_DELETE_PARTNER = "module.stock.billetterie.message.deletePartner.confirmation";
    private static final String MESSAGE_DELETE_PARTNER_WITH_SHOW = "module.stock.billetterie.message.deletePartner.with.show";
    private static final String MESSAGE_ERROR_NO_COMMENT_IF_NOT_ACCESSIBLE = "module.stock.billetterie.message.noCommentIfNotAccessible";

    // MEMBERS VARIABLES
    private IProviderService _serviceProvider;
    private ProviderFilter _providerFilter;
    private IShowService _serviceShow;
    private DistrictService _serviceDistrict;

    /**
     * Instantiates a new partner jsp bean.
     */
    public PartnerJspBean( )
    {
        super( );
        _providerFilter = new ProviderFilter( );
        _serviceProvider = SpringContextService.getContext( ).getBean( IProviderService.class );
        _serviceShow = (IShowService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SHOW_SERVICE );
        _serviceDistrict = SpringContextService.getContext( ).getBean( DistrictService.class );
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
        orderList.add( BilletterieConstants.NAME );
        filter.setOrders( orderList );
        filter.setOrderAsc( true );

        //si un objet est déjà présent en session, on l'utilise
        DataTableManager<PartnerDTO> dataTableToUse = getDataTable( request, filter );

        // Fill the model
        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_DATA_TABLE_PARTNER, dataTableToUse );

        // the filter
        model.put( TicketsConstants.MARK_FILTER, filter );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PARTNERS, getLocale( ), model );

        //opération nécessaire pour eviter les fuites de mémoires
        dataTableToUse.clearItems( );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Get the DataTableManager object for the PartnerDTO bean
     * @param request the http request
     * @param filter the filter
     * @param <T> the bean type of the data table manager
     * @return the data table to use
     */
    private <T> DataTableManager<T> getDataTable( HttpServletRequest request, ProviderFilter filter )
    {
        //si un objet est déjà présent en session, on l'utilise
        Method findMethod = null;
        try
        {
            findMethod = _serviceProvider.getClass( ).getMethod( PARAMETER_FIND_BY_FILTER_NAME_METHOD,
                    ProviderFilter.class, PaginationProperties.class );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Erreur lors de l'obtention du data table : ", e );
        }
        DataTableManager<T> dataTableToUse = getAbstractDataTableManager( request, filter, MARK_DATA_TABLE_PARTNER,
                JSP_MANAGE_PARTNERS, _serviceProvider, findMethod );

        //si pas d'objet en session, il faut ajouter les colonnes à afficher
        if ( dataTableToUse.getListColumn( ).isEmpty( ) )
        {
            dataTableToUse.addColumn( "module.stock.billetterie.manage_category.filter.name",
                    "name", true );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.manage_category.actionsLabel",
                    MACRO_COLUMN_ACTIONS_PARTNER );
        }
        saveDataTableInSession( request, dataTableToUse, MARK_DATA_TABLE_PARTNER );
        return dataTableToUse;
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
            model.put( BilletterieConstants.ERROR, getHtmlError( ve ) );
        }
        else
        {
            // No error, get provider if modify
            String strProviderId = request.getParameter( PARAMETER_PARTNER_ID );
            if ( StringUtils.isNotBlank( strProviderId ) )
            {
                setPageTitleProperty( PAGE_TITLE_MODIFY_PARTNER );
                int nIdProvider = Integer.parseInt( strProviderId );

                // Origin of the user : ManagePartner.jsp, so no provider attributs in the request, 
                //                      SavePartner, so attributs present in the request and MUSTN'T be load with DAO
                provider = request.getParameter( PARAMETER_NAME ) == null ? _serviceProvider
                        .findByIdWithProducts( nIdProvider ) : new PartnerDTO( );
            }
            else
            {
                setPageTitleProperty( PAGE_TITLE_CREATE_PARTNER );
                // Create new Partner
                provider = new PartnerDTO( );
                // now, provider are set with input take in request (case of add or delete contact)
            }

            //need to populate in case of add or remove contact
            populate( provider, request );

            //search if process must delete a contact
            for ( Object requestParameter : request.getParameterMap( ).keySet( ) )
            {
                if ( ( (String) requestParameter ).startsWith( PARAMETER_DEL_CONTACT ) )
                {
                    String strIdContactToDelete = ( (String) requestParameter ).substring( PARAMETER_DEL_CONTACT
                            .length( ) );
                    int idContactToDelete = Integer.valueOf( strIdContactToDelete );
                    provider.removeContact( idContactToDelete );
                    break;
                }
            }

            //add an empty contact
            if ( request.getParameter( PartnerJspBean.PARAMETER_ADD_CONTACT ) != null )
            {
                provider.addEmptyContact( );
            }

        }

        // Add the JSP which called this action
        model.put( StockConstants.MARK_JSP_BACK, request.getParameter( StockConstants.MARK_JSP_BACK ) );

        //        String strNumberContact = request.getParameter( MARK_PARTNER_NUMBER_CONTACT );
        //        int numberContact = strNumberContact == null ? 0 : Integer.valueOf( strNumberContact );
        if ( request.getParameter( PARAMETER_ADD_CONTACT ) != null )
        {
            //            numberContact++;
            populate( provider, request );
        }
        model.put( MARK_PARTNER, provider );

        ArrayList<Contact> listContactOrderById = (ArrayList<Contact>) provider.getContactList( );
        Collections.sort( listContactOrderById, Contact.COMPARATOR_USING_ID );
        model.put( MARK_LIST_CONTACT, listContactOrderById );
        
        DistrictFilter districtFilter = new DistrictFilter();
        List<String> order = new ArrayList<String>();
        order.add( "_libelle" );
        districtFilter.setOrders( order );

        List<District> listDistrict = _serviceDistrict.findByFilter( districtFilter, null );
        model.put( MARK_LIST_DISTRICT, listDistrict );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SAVE_PARTNER, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Save a partner, use to add contact
     * @param request The HTTP request
     * @return redirection url
     */
    public String doSavePartner( HttpServletRequest request )
    {
        if ( null != request.getParameter( StockConstants.PARAMETER_BUTTON_CANCEL ) )
        {
            return doGoBack( request );
        }

        PartnerDTO provider = new PartnerDTO( );
        populate( provider, request );

        try
        {
            // Controls mandatory fields
            validateBilletterie( provider );
            List<ValidationError> errors = validate( provider, "" );
            if ( errors.size( ) > 0 )
            {
                return AdminMessageService.getMessageUrl( request, Messages.MESSAGE_INVALID_ENTRY, errors );
            }

            _serviceProvider.doSaveProvider( provider );

            if ( !provider.isAccessible( ) && provider.getAccessibleComment( ) != StringUtils.EMPTY )
            {
                throw new BusinessException( provider, MESSAGE_ERROR_NO_COMMENT_IF_NOT_ACCESSIBLE );
            }
        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, JSP_SAVE_PARTNER );
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

        UrlItem url = new UrlItem( JSP_DO_DELETE_PARTNER );

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
            return AdminMessageService
                    .getMessageUrl( request, MESSAGE_DELETE_PARTNER_WITH_SHOW, AdminMessage.TYPE_STOP );
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
