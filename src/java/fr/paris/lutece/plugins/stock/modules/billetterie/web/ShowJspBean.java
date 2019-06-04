/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import fr.paris.lutece.plugins.stock.business.category.CategoryFilter;
import fr.paris.lutece.plugins.stock.business.product.ProductFilter;
import fr.paris.lutece.plugins.stock.business.provider.ProviderFilter;
import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.billetterie.utils.constants.BilletterieConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.business.PartnerDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ICategoryService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IProviderService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ISeanceService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IStatisticService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.service.ISubscriptionProductService;
import fr.paris.lutece.plugins.stock.utils.ImageUtils;
import fr.paris.lutece.plugins.stock.utils.ListUtils;
import fr.paris.lutece.plugins.stock.utils.constants.StockConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.resource.ExtendableResourceRemovalListenerService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.resource.ExtendableResourcePluginActionManager;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.datatable.DataTableManager;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * The Class ShowJspBean.
 */
public class ShowJspBean extends AbstractJspBean
{
    /** The Constant LOGGER. */
    public static final Logger LOGGER = Logger.getLogger( ShowJspBean.class );

    /** The Constant PARAMETER_CATEGORY_ID. */
    public static final String PARAMETER_CATEGORY_ID = "product_id";

    /** The Constant PARAMETER_PRODUCT_PARTNER_ID. */
    public static final String PARAMETER_PRODUCT_PARTNER_ID = "partnerId";

    /** The Constant PARAMETER_PRODUCT_CATEGORY_ID. */
    public static final String PARAMETER_PRODUCT_CATEGORY_ID = "categoryId";

    /** The Constant RIGHT_MANAGE_PRODUCTS. */
    public static final String RIGHT_MANAGE_PRODUCTS = "PRODUCTS_MANAGEMENT";

    /** The Constant MARK_PRODUCT. */
    public static final String MARK_PRODUCT = "product";

    /** The Constant MARK_TITLE. */
    public static final String MARK_TITLE = "title";

    /** The constants for DataTableManager */
    public static final String MARK_DATA_TABLE_PRODUCT = "dataTableProduct";
    public static final String MARK_FILTER_PRODUCT = "filterProduct";
    public static final String MACRO_COLUMN_ACTIONS_PRODUCT = "columnActionsProduct";
    public static final String MACRO_COLUMN_NAME_PRODUCT = "columnNameProduct";
    public static final String MACRO_COLUMN_DATES_PRODUCT = "columnDatesProduct";

    /** The Constant PARAMETER_PRODUCT_TYPE_LIST. */
    public static final String PARAMETER_PRODUCT_TYPE_LIST = "product_type_list";
    private static final long serialVersionUID = 5782544563181019800L;

    /** The Constant PARAMETER_PRODUCT_TYPE_LIST_DEFAULT. */
    public static final String PARAMETER_PRODUCT_TYPE_LIST_DEFAULT = "product_type_list_default";

    /** The Constant BEAN_STOCK_TICKETS_SEANCE_SERVICE. */
    private static final String BEAN_STOCK_TICKETS_SEANCE_SERVICE = "stock-tickets.seanceService";

    /** The Constant BEAN_STOCK_TICKETS_SHOW_SERVICE. */
    private static final String BEAN_STOCK_TICKETS_SHOW_SERVICE = "stock-tickets.showService";

    /** The Constant MARK_LIST_CATEGORIES. */
    private static final String MARK_LIST_CATEGORIES = "category_list";

    /** The Constant MARK_LIST_PROVIDERS. */
    private static final String MARK_LIST_PROVIDERS = "provider_list";
    private static final String MARK_CONTACT_LIST = "contact_list";

    private static final String PARAMETER_PRODUCT_ID_PROVIDER = "idProvider";
    public static final String PARAMETER_REFRESH_CONTACT = "refresh_contact";

    private static final String MARK_URL_POSTER = "url_poster";

    /** The Constant MARK_PUBLIC_LIST. */
    private static final String MARK_PUBLIC_LIST = "public_list";

    /** The Constant PARAMETER_POSTER. */
    private static final String PARAMETER_POSTER = "posterFile";

    /** The Constant PARAMETER_A_LAFFICHE. */
    private static final String PARAMETER_A_LAFFICHE = "aLaffiche";

    /** The Constant PROPERTY_POSTER_WIDTH. */
    private static final String PROPERTY_POSTER_THUMB_WIDTH = "stock-billetterie.poster.width";

    /** The Constant PROPERTY_POSTER_HEIGHT. */
    private static final String PROPERTY_POSTER_THUMB_HEIGHT = "stock-billetterie.poster.height";
    private static final String PROPERTY_POSTER_MAX_HEIGHT = "stock-billetterie.poster.max-height";
    private static final String PROPERTY_POSTER_MAX_WIDTH = "stock-billetterie.poster.max-width";
    private static final int PROPERTY_POSTER_MAX_HEIGHT_DEFAULT = 400;
    private static final int PROPERTY_POSTER_MAX_WIDTH_DEFAULT = 400;
    private static final String PROPERTY_POSTER_PATH = "stock-billetterie.poster.path";
    private static final String PROPERTY_RIMG_POSTER_PATH = "stock-billetterie.poster.rimg.path";

    /** The Constant PROPERTY_STOCK_BILLETTERIE_SHOW_PUBLIC. */
    private static final String PROPERTY_STOCK_BILLETTERIE_SHOW_PUBLIC = "stock-billetterie.show.public";

    /** The Constant MARK_WEBAPP_URL. */
    private static final String MARK_WEBAPP_URL = "webapp_url";

    /** The Constant MARK_LOCALE. */
    private static final String MARK_LOCALE = "locale";

    // I18N
    /** The Constant PAGE_TITLE_MANAGE_PRODUCT. */
    private static final String PAGE_TITLE_MANAGE_PRODUCT = "module.stock.billetterie.manage_product.title";

    /** The Constant PAGE_TITLE_CREATE_PRODUCT. */
    private static final String PAGE_TITLE_CREATE_PRODUCT = "module.stock.billetterie.save_product.create.title";

    /** The Constant PAGE_TITLE_MODIFY_PRODUCT. */
    private static final String PAGE_TITLE_MODIFY_PRODUCT = "module.stock.billetterie.save_product.title";

    // Properties

    // JSP
    /** The Constant JSP_MANAGE_PRODUCTS. */
    private static final String JSP_MANAGE_PRODUCTS = "jsp/admin/plugins/stock/modules/billetterie/ManageProducts.jsp";

    /** The Constant JSP_SAVE_PRODUCT. */
    private static final String JSP_SAVE_PRODUCT = "SaveProduct.jsp";

    /** The Constant CATEGORY_DO_DELETE_JSP. */
    private static final String CATEGORY_DO_DELETE_JSP = "jsp/admin/plugins/stock/modules/billetterie/DoDeleteProduct.jsp";

    // Templates
    /** The Constant TEMPLATE_MANAGE_PRODUCTS. */
    private static final String TEMPLATE_MANAGE_PRODUCTS = "admin/plugins/stock/modules/billetterie/manage_products.html";

    /** The Constant TEMPLATE_SAVE_PRODUCT. */
    private static final String TEMPLATE_SAVE_PRODUCT = "admin/plugins/stock/modules/billetterie/save_product.html";

    //
    // // Mark

    // MESSAGES
    /** The Constant MESSAGE_CONFIRMATION_DELETE_PRODUCT. */
    private static final String MESSAGE_CONFIRMATION_DELETE_PRODUCT = "module.stock.billetterie.message.deleteProduct.confirmation";

    /** The Constant MESSAGE_DELETE_SHOW_WITH_SEANCE. */
    private static final String MESSAGE_DELETE_SHOW_WITH_SEANCE = "module.stock.billetterie.message.deleteProduct.with.seance";

    /** The Constant MESSAGE_ERROR_MANDATORY_POSTER. */
    private static final String MESSAGE_ERROR_MANDATORY_POSTER = "module.stock.billetterie.message.error.mandatory_poster";

    /** The Constant ERROR_MESSAGE_GET_AFFICHE. */
    private static final String MESSAGE_ERROR_GET_AFFICHE = "module.stock.billetterie.message.error.get.affiche";

    // MEMBERS VARIABLES
    /** The _service product. */
    // @Inject
    // @Named( "stock-tickets.showService" )
    private IShowService _serviceProduct;

    /** The _service offer. */
    // @Inject
    // @Named( "stock-tickets.seanceService" )
    private ISeanceService _serviceOffer;

    /** The _service provider. */
    // @Inject
    private IProviderService _serviceProvider;

    /** The _service category. */
    // @Inject
    private ICategoryService _serviceCategory;

    /** The _service statistic. */
    // @Inject
    private IStatisticService _serviceStatistic;
    private ISubscriptionProductService _subscriptionProductService;

    /** The _product filter. */
    private transient ProductFilter _productFilter;

    /**
     * Instantiates a new show jsp bean.
     */
    public ShowJspBean( )
    {
        _productFilter = new ShowFilter( );
        _serviceProduct = (IShowService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SHOW_SERVICE );
        _serviceOffer = (ISeanceService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SEANCE_SERVICE );
        _serviceProvider = SpringContextService.getContext( ).getBean( IProviderService.class );
        _serviceCategory = SpringContextService.getContext( ).getBean( ICategoryService.class );
        _serviceStatistic = SpringContextService.getContext( ).getBean( IStatisticService.class );
        _subscriptionProductService = SpringContextService.getContext( ).getBean( ISubscriptionProductService.class );
    }

    /**
     * Generates a HTML form that displays all products.
     *
     * @param request
     *            the Http request
     * @return HTML
     */
    public String getManageProducts( HttpServletRequest request )
    {
        String strToDate = getToday( );

        setPageTitleProperty( PAGE_TITLE_MANAGE_PRODUCT );

        ProductFilter filter = getProductFilter( request );
        List<String> orderList = new ArrayList<String>( );
        orderList.add( BilletterieConstants.NAME );
        filter.setOrderAsc( true );
        if ( filter.getOrders( ) != null && filter.getOrders( ).size( ) > 0 )
        {
            filter.setOrders( filter.getOrders( ) );
        }
        else
        {
            filter.setOrders( orderList );
        }

        if ( filter.getDateFrom( ) == null )
        {
            filter.setDateFrom( strToDate );
        }

        DataTableManager<ShowDTO> dataTableToUse = getDataTable( request, filter );

        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_DATA_TABLE_PRODUCT, dataTableToUse );

        // Fill the model
        model.put( TicketsConstants.MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );

        // Combo
        ProviderFilter providerFilter = new ProviderFilter( );
        providerFilter.setOrderAsc( true );
        providerFilter.setOrders( orderList );

        ReferenceList providerComboList = ListUtils.toReferenceList( _serviceProvider.findByFilter( providerFilter, null ), BilletterieConstants.ID,
                BilletterieConstants.NAME, StockConstants.EMPTY_STRING );
        CategoryFilter categoryFilter = new CategoryFilter( );
        categoryFilter.setOrderAsc( true );
        categoryFilter.setOrders( orderList );

        ReferenceList categoryComboList = ListUtils.toReferenceList( _serviceCategory.findByFilter( categoryFilter, null ), BilletterieConstants.ID,
                BilletterieConstants.NAME, StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_PROVIDERS, providerComboList );
        model.put( MARK_LIST_CATEGORIES, categoryComboList );
        // the filter
        model.put( TicketsConstants.MARK_FILTER, filter );
        model.put( MARK_LOCALE, getLocale( ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PRODUCTS, getLocale( ), model );

        // opération nécessaire pour eviter les fuites de mémoires
        dataTableToUse.clearItems( );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Get the DataTableManager object for the ShowDTO bean
     * 
     * @param request
     *            the http request
     * @param filter
     *            the filter
     * @return the data table to use
     */
    private DataTableManager<ShowDTO> getDataTable( HttpServletRequest request, ProductFilter filter )
    {
        // si un objet est déjà présent en session, on l'utilise
        Method findMethod = null;

        try
        {
            findMethod = _serviceProduct.getClass( ).getMethod( PARAMETER_FIND_BY_FILTER_NAME_METHOD, ProductFilter.class, PaginationProperties.class );
        }
        catch( Exception e )
        {
            LOGGER.error( "Erreur lors de l'obtention du data table : ", e );
        }

        DataTableManager<ShowDTO> dataTableToUse = getAbstractDataTableManager( request, filter, MARK_DATA_TABLE_PRODUCT, JSP_MANAGE_PRODUCTS, _serviceProduct,
                findMethod );

        // si pas d'objet en session, il faut ajouter les colonnes à afficher
        if ( dataTableToUse.getListColumn( ).isEmpty( ) )
        {
            dataTableToUse.addColumn( "module.stock.billetterie.manage_product.filter.name", "name", true );
            dataTableToUse.addColumn( "module.stock.billetterie.manage_product.filter.provider", "providerName", true );
            dataTableToUse.addColumn( "module.stock.billetterie.manage_product.filter.category", "categoryName", true );
            // dataTableToUse.addColumn("module.stock.billetterie.manage_product.filter.date_from", "startDate", true);
            // dataTableToUse.addColumn("module.stock.billetterie.manage_product.filter.date_to", "endDate", true);
            dataTableToUse.addFreeColumn( "module.stock.billetterie.manage_product.dates", MACRO_COLUMN_DATES_PRODUCT );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.manage_product.actionsLabel", MACRO_COLUMN_ACTIONS_PRODUCT );
        }

        saveDataTableInSession( request, dataTableToUse, MARK_DATA_TABLE_PRODUCT );

        return dataTableToUse;
    }

    private ReferenceList getContactComboList( int idProvider )
    {
        PartnerDTO findById = _serviceProvider.findById( idProvider );
        ReferenceList contactComboList = ListUtils.toReferenceList( findById.getContactList( ), BilletterieConstants.ID, BilletterieConstants.NAME,
                StockConstants.EMPTY_STRING );
        return contactComboList;
    }

    /**
     * Returns the form to modify a provider.
     *
     * @param request
     *            The Http request
     * @param strProductClassName
     *            The class name of the provider entity to modify
     * @return the html code of the provider form
     */
    @Transactional( readOnly = true )
    public String getSaveProduct( HttpServletRequest request, String strProductClassName )
    {
        ShowDTO product = null;
        Map<String, Object> model = new HashMap<String, Object>( );

        FunctionnalException fe = getErrorOnce( request );

        if ( fe != null )
        {
            product = (ShowDTO) fe.getBean( );
            model.put( BilletterieConstants.ERROR, getHtmlError( fe ) );
        }
        else
        {
            String strProductId = request.getParameter( PARAMETER_CATEGORY_ID );
            String strPartnerId = request.getParameter( PARAMETER_PRODUCT_PARTNER_ID );
            String strCategoryId = request.getParameter( PARAMETER_PRODUCT_CATEGORY_ID );

            if ( strProductId != null )
            {
                setPageTitleProperty( PAGE_TITLE_MODIFY_PRODUCT );

                int nIdProduct = Integer.parseInt( strProductId );
                product = _serviceProduct.findById( nIdProduct );

                int idProvider = product.getIdProvider( );

                if ( request.getParameter( PARAMETER_REFRESH_CONTACT ) != null )
                {
                    // case of wanting to get the contact which can be link to
                    // the product
                    populate( product, request );
                    String strIdProvider = request.getParameter( PARAMETER_PRODUCT_ID_PROVIDER );
                    int nIdSelectedProvider = Integer.valueOf( strIdProvider );

                    if ( nIdSelectedProvider >= 0 )
                    {
                        idProvider = nIdSelectedProvider;
                    }

                }

                model.put( MARK_CONTACT_LIST, getContactComboList( idProvider ) );

                _serviceProduct.correctProduct( product );
            }
            else
            {
                setPageTitleProperty( PAGE_TITLE_CREATE_PRODUCT );
                product = new ShowDTO( );

                // If creation and filter "Salle" exist, pre-select the partner
                if ( StringUtils.isNotBlank( strPartnerId ) )
                {
                    product.setIdProvider( Integer.parseInt( strPartnerId ) );
                }
                else
                {
                    populate( product, request );
                }

                // If creation and filter "Categorie" exist, pre-select the
                // category
                if ( StringUtils.isNotBlank( strCategoryId ) )
                {
                    product.setIdCategory( Integer.parseInt( strCategoryId ) );
                }

                if ( ( request.getParameter( PARAMETER_PRODUCT_ID_PROVIDER ) != null ) && !request.getParameter( PARAMETER_PRODUCT_ID_PROVIDER ).equals( "-1" ) )
                {
                    Integer idProvider = Integer.valueOf( request.getParameter( PARAMETER_PRODUCT_ID_PROVIDER ) );

                    model.put( MARK_CONTACT_LIST, getContactComboList( idProvider ) );
                }
            }
        }

        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            FileItem fileItem = multipartRequest.getFile( PARAMETER_POSTER );

            if ( ( fileItem != null ) && ( fileItem.getSize( ) > 0 ) )
            {
                // writePoster( request, product );
                product.setPosterName( fileItem.getName( ) );
                populate( product, request );
            }
        }

        // Combo
        List<String> orderList = new ArrayList<String>( );
        orderList.add( BilletterieConstants.NAME );

        ProviderFilter providerFilter = new ProviderFilter( );
        providerFilter.setOrderAsc( true );
        providerFilter.setOrders( orderList );

        ReferenceList providerComboList = ListUtils.toReferenceList( _serviceProvider.findByFilter( providerFilter, null ), BilletterieConstants.ID,
                BilletterieConstants.NAME, StockConstants.EMPTY_STRING );
        CategoryFilter categoryFilter = new CategoryFilter( );
        categoryFilter.setOrderAsc( true );
        categoryFilter.setOrders( orderList );

        ReferenceList categoryComboList = ListUtils.toReferenceList( _serviceCategory.findByFilter( categoryFilter, null ), BilletterieConstants.ID,
                BilletterieConstants.NAME, StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_PROVIDERS, providerComboList );
        model.put( MARK_LIST_CATEGORIES, categoryComboList );
        model.put( MARK_PUBLIC_LIST, ListUtils.getPropertyList( PROPERTY_STOCK_BILLETTERIE_SHOW_PUBLIC ) );

        model.put( StockConstants.MARK_JSP_BACK, request.getParameter( StockConstants.MARK_JSP_BACK ) );
        model.put( MARK_PRODUCT, product );
        model.put( MARK_URL_POSTER, AppPropertiesService.getProperty( PROPERTY_RIMG_POSTER_PATH ) );

        // Richtext editor parameters
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, getLocale( ) );

        if ( ( product.getId( ) != null ) && ( product.getId( ) != 0 ) )
        {
            model.put( MARK_TITLE, I18nService.getLocalizedString( PAGE_TITLE_MODIFY_PRODUCT, Locale.getDefault( ) ) );
        }
        else
        {
            model.put( MARK_TITLE, I18nService.getLocalizedString( PAGE_TITLE_CREATE_PRODUCT, Locale.getDefault( ) ) );
        }

        ExtendableResourcePluginActionManager.fillModel( request, getUser( ), model, String.valueOf( product.getId( ) ), ShowDTO.PROPERTY_RESOURCE_TYPE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SAVE_PRODUCT, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Save a product.
     *
     * @param request
     *            The HTTP request
     * @return redirection url
     */
    public String doSaveProduct( HttpServletRequest request )
    {
        if ( null != request.getParameter( StockConstants.PARAMETER_BUTTON_CANCEL ) )
        {
            return doGoBack( request );
        }

        String strUpdateDate = getToday( );

        ShowDTO product = new ShowDTO( );
        populate( product, request );

        product.setUpdateDate( strUpdateDate );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_A_LAFFICHE ) ) )
        {
            product.setAlaffiche( true );
        }

        try
        {
            // Get and save the poster into temp files
            File [ ] filePosterArray = writePoster( request, product );

            // Controls mandatory fields
            validateBilletterie( product );

            // Persist entity
            ShowDTO saveProduct = _serviceProduct.doSaveProduct( product, filePosterArray );

            // Statistic management
            _serviceStatistic.doManageProductSaving( saveProduct );
        }
        catch( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, JSP_SAVE_PRODUCT );
        }

        return doGoBack( request );
    }

    /**
     * Get the poster in the request and write it on the disk.
     *
     * @param request
     *            http request (multipart if contains poster)
     * @param product
     *            product entity
     * @return the poster image file (0 : thumbnail, 1 : full size)
     * @throws BusinessException
     *             the business exception
     */
    private File [ ] writePoster( HttpServletRequest request, ShowDTO product ) throws BusinessException
    {
        File [ ] filePosterArray = null;
        boolean fileGotten = false;

        // File uploaded
        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            FileItem fileItem = multipartRequest.getFile( PARAMETER_POSTER );

            if ( ( fileItem != null ) && ( fileItem.getSize( ) > 0 ) )
            {
                InputStream fisPoster = null;
                InputStream realFisPoster = null;

                try
                {
                    fisPoster = fileItem.getInputStream( );
                    realFisPoster = fileItem.getInputStream( );

                    // File fPoster = new File( new File( posterFolderPath ),
                    // fileItem.getName( ) );
                    File fPoster = File.createTempFile( FilenameUtils.getBaseName( fileItem.getName( ) ), null );
                    File realPoster = File.createTempFile( FilenameUtils.getBaseName( fileItem.getName( ) ), null );

                    // Generate unique name
                    fPoster = fr.paris.lutece.plugins.stock.utils.FileUtils.getUniqueFile( fPoster );
                    realPoster = fr.paris.lutece.plugins.stock.utils.FileUtils.getUniqueFile( realPoster );

                    // Store poster picture
                    fr.paris.lutece.plugins.stock.utils.FileUtils.writeInputStreamToFile( fisPoster, fPoster );
                    fr.paris.lutece.plugins.stock.utils.FileUtils.writeInputStreamToFile( realFisPoster, realPoster );

                    File fPosterResize = ImageUtils.resizeImage( fPoster,
                            AppPropertiesService.getPropertyInt( PROPERTY_POSTER_MAX_WIDTH, PROPERTY_POSTER_MAX_WIDTH_DEFAULT ),
                            AppPropertiesService.getPropertyInt( PROPERTY_POSTER_MAX_HEIGHT, PROPERTY_POSTER_MAX_HEIGHT_DEFAULT ), null );

                    // Create a thumbnail image
                    File fTbPoster = ImageUtils.createThumbnail( fPoster, AppPropertiesService.getPropertyInt( PROPERTY_POSTER_THUMB_WIDTH, 120 ),
                            AppPropertiesService.getPropertyInt( PROPERTY_POSTER_THUMB_HEIGHT, 200 ) );

                    // Save file name into entity
                    product.setPosterName( fPoster.getName( ) );
                    fileGotten = true;

                    filePosterArray = new File [ ] {
                            fTbPoster, fPosterResize, realPoster
                    };
                }
                catch( IOException e )
                {
                    throw new BusinessException( product, MESSAGE_ERROR_GET_AFFICHE );
                }
                finally
                {
                    if ( fisPoster != null )
                    {
                        try
                        {
                            fisPoster.close( );
                        }
                        catch( IOException e )
                        {
                            AppLogService.error( e.getMessage( ), e );
                        }
                    }
                }
            }
        }

        if ( !fileGotten )
        {
            // Error mandatory poster
            if ( StringUtils.isEmpty( product.getPosterName( ) ) )
            {
                throw new BusinessException( product, MESSAGE_ERROR_MANDATORY_POSTER );
            }
        }

        return filePosterArray;
    }

    /**
     * Return the url of the JSP which called the last action.
     *
     * @param request
     *            The Http request
     * @return The url of the last JSP
     */
    private String doGoBack( HttpServletRequest request )
    {
        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

        return StringUtils.isNotBlank( strJspBack ) ? ( AppPathService.getBaseUrl( request ) + strJspBack )
                : ( AppPathService.getBaseUrl( request ) + JSP_MANAGE_PRODUCTS );
    }

    /**
     * Gets the product filter.
     *
     * @param request
     *            the request
     * @return the product filter
     */
    private ProductFilter getProductFilter( HttpServletRequest request )
    {
        // SORT
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        // "filter" in request ==> new filter. use old one otherwise
        // if ( request.getParameter( TicketsConstants.PARAMETER_FILTER ) !=
        // null )
        // {
        ProductFilter filter = new ShowFilter( );
        populate( filter, request );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_A_LAFFICHE ) ) )
        {
            filter.setAlaffiche( true );
        }

        _productFilter = filter;

        // }
        if ( strSortedAttributeName != null )
        {
            _productFilter.getOrders( ).add( strSortedAttributeName );

            String strAscSort = request.getParameter( Parameters.SORTED_ASC );
            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );
            _productFilter.setOrderAsc( bIsAscSort );
        }

        return _productFilter;
    }

    /**
     * Returns the confirmation message to delete a product.
     *
     * @param request
     *            The Http request
     * @return the html code message
     */
    public String getDeleteProduct( HttpServletRequest request )
    {
        String strProductId = request.getParameter( PARAMETER_CATEGORY_ID );

        int nIdProduct;

        try
        {
            nIdProduct = Integer.parseInt( strProductId );
        }
        catch( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR, AdminMessage.TYPE_STOP );
        }

        Map<String, Object> urlParam = new HashMap<String, Object>( );
        urlParam.put( PARAMETER_CATEGORY_ID, nIdProduct );

        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

        if ( StringUtils.isNotBlank( strJspBack ) )
        {
            urlParam.put( StockConstants.MARK_JSP_BACK, strJspBack );
        }

        // Only product without seance can be delete
        SeanceFilter filter = new SeanceFilter( );
        filter.setProductId( nIdProduct );

        ResultList<SeanceDTO> bookingList = _serviceOffer.findByFilter( filter, null );

        if ( ( bookingList != null ) && !bookingList.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_DELETE_SHOW_WITH_SEANCE, AdminMessage.TYPE_STOP );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DELETE_PRODUCT, CATEGORY_DO_DELETE_JSP, AdminMessage.TYPE_CONFIRMATION,
                urlParam );
    }

    /**
     * Delete a product.
     *
     * @param request
     *            The Http request
     * @return the html code message
     */
    public String doDeleteProduct( HttpServletRequest request )
    {
        String strProductId = request.getParameter( PARAMETER_CATEGORY_ID );

        int nIdProduct;

        try
        {
            nIdProduct = Integer.parseInt( strProductId );
        }
        catch( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR, AdminMessage.TYPE_STOP );
        }

        // On supprime les statistiques du spectacle
        _serviceStatistic.doRemoveProductStatisticByIdProduct( nIdProduct );

        // delete the subscription link to the product
        _subscriptionProductService.doDeleteByIdProduct( strProductId );

        _serviceProduct.doDeleteProduct( nIdProduct );

        // on supprime toutes référence à ce spectacle dans le plugin extends
        ExtendableResourceRemovalListenerService.doRemoveResourceExtentions( ShowDTO.PROPERTY_RESOURCE_TYPE, Integer.toString( nIdProduct ) );

        return doGoBack( request );
    }

    /**
     * Return the today date.
     * 
     * @return the today date
     */
    public String getToday( )
    {
        String format = "dd/MM/yyyy";
        SimpleDateFormat formater = new java.text.SimpleDateFormat( format );
        Date date = new java.util.Date( );
        return formater.format( date );
    }
}
