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

import fr.paris.lutece.plugins.stock.business.category.CategoryFilter;
import fr.paris.lutece.plugins.stock.business.product.ProductFilter;
import fr.paris.lutece.plugins.stock.business.provider.ProviderFilter;
import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.commons.exception.TechnicalException;
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
import fr.paris.lutece.plugins.stock.utils.ImageUtils;
import fr.paris.lutece.plugins.stock.utils.ListUtils;
import fr.paris.lutece.plugins.stock.utils.constants.StockConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.DelegatePaginator;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;


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

    /** The Constant PARAMETER_PRODUCT_TYPE_LIST. */
    public static final String PARAMETER_PRODUCT_TYPE_LIST = "product_type_list";

    /** The Constant PARAMETER_PRODUCT_TYPE_LIST_DEFAULT. */
    public static final String PARAMETER_PRODUCT_TYPE_LIST_DEFAULT = "product_type_list_default";

    /** The Constant MARK_LIST_PRODUCTS. */
    private static final String MARK_LIST_PRODUCTS = "list_products";

    /** The Constant MARK_LIST_CATEGORIES. */
    private static final String MARK_LIST_CATEGORIES = "category_list";

    /** The Constant MARK_LIST_PROVIDERS. */
    private static final String MARK_LIST_PROVIDERS = "provider_list";

    private static final String MARK_URL_POSTER = "url_poster";

    /** The Constant PARAMETER_POSTER. */
    private static final String PARAMETER_POSTER = "posterFile";

    /** The Constant PROPERTY_POSTER_WIDTH. */
    private static final String PROPERTY_POSTER_WIDTH = "stock-billetterie.poster.width";

    /** The Constant PROPERTY_POSTER_HEIGHT. */
    private static final String PROPERTY_POSTER_HEIGHT = "stock-billetterie.poster.height";

    private static final String PROPERTY_POSTER_PATH = "stock-billetterie.poster.path";

    // I18N
    /** The Constant PAGE_TITLE_MANAGE_PRODUCT. */
    private static final String PAGE_TITLE_MANAGE_PRODUCT = "module.stock.billetterie.manage_product.title";

    /** The Constant PAGE_TITLE_CREATE_PRODUCT. */
    private static final String PAGE_TITLE_CREATE_PRODUCT = "module.stock.billetterie.create_product.title";

    /** The Constant PAGE_TITLE_MODIFY_PRODUCT. */
    private static final String PAGE_TITLE_MODIFY_PRODUCT = "module.stock.billetterie.save_product.title";

    // Properties
    /** The Constant POSTER_FOLDER_PATH. */
    private static final String POSTER_FOLDER_PATH = "stock-billetterie.poster.folder.path";

    // JSP
    /** The Constant JSP_MANAGE_PRODUCTS. */
    private static final String JSP_MANAGE_PRODUCTS = "jsp/admin/plugins/stock/modules/billetterie/ManageProducts.jsp";

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


    // Variables
    /** The _n items per page. */
    private int _nItemsPerPage;

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

    /** The _product filter. */
    private ProductFilter _productFilter;

    /**
     * Instantiates a new show jsp bean.
     */
    public ShowJspBean( )
    {
        _productFilter = new ShowFilter( );
        _serviceProduct = (IShowService) SpringContextService.getBean( "stock-tickets.showService" );
        _serviceOffer = (ISeanceService) SpringContextService.getBean( "stock-tickets.seanceService" );
        _serviceProvider = SpringContextService.getContext( ).getBean( IProviderService.class );
        _serviceCategory = SpringContextService.getContext( ).getBean( ICategoryService.class );
        _serviceStatistic = SpringContextService.getContext( ).getBean( IStatisticService.class );
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
        setPageTitleProperty( PAGE_TITLE_MANAGE_PRODUCT );

        ProductFilter filter = getProductFilter( request );
        List<String> orderList = new ArrayList<String>( );
        orderList.add( "name" );
        filter.setOrderAsc( true );
        filter.setOrders( orderList );
        
        ResultList<ShowDTO> listAllProduct = _serviceProduct
                .findByFilter( filter, getPaginationProperties( request ) );

        DelegatePaginator<ShowDTO> paginator = getPaginator( request, listAllProduct );


        // Fill the model
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( TicketsConstants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( TicketsConstants.MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_PRODUCTS, paginator.getPageItems( ) );

        // Combo
        ProviderFilter providerFilter = new ProviderFilter( );
        providerFilter.setOrderAsc( true );
        providerFilter.setOrders( orderList );
        ReferenceList providerComboList = ListUtils.toReferenceList( _serviceProvider.findByFilter( providerFilter, null ), "id", "name",
        		StockConstants.EMPTY_STRING );
        CategoryFilter categoryFilter = new CategoryFilter( );
        categoryFilter.setOrderAsc( true );
        categoryFilter.setOrders( orderList );
        ReferenceList categoryComboList = ListUtils.toReferenceList( _serviceCategory.findByFilter( categoryFilter, null ), "id", "name",
                StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_PROVIDERS, providerComboList );
        model.put( MARK_LIST_CATEGORIES, categoryComboList );
        // the filter
        model.put( TicketsConstants.MARK_FILTER, filter );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PRODUCTS, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Returns the form to modify a provider.
     * 
     * @param request The Http request
     * @param strProductClassName The class name of the provider entity to
     *            modify
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
            model.put( "error", getHtmlError( fe ) );
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
                // If creation and filter "Categorie" exist, pre-select the category
                if ( StringUtils.isNotBlank( strCategoryId ) )
                {
                    product.setIdCategory( Integer.parseInt( strCategoryId ) );
                }
            }
        }

        // Combo
        List<String> orderList = new ArrayList<String>( );
        orderList.add( "name" );
        ProviderFilter providerFilter = new ProviderFilter( );
        providerFilter.setOrderAsc( true );
        providerFilter.setOrders( orderList );
        ReferenceList providerComboList = ListUtils.toReferenceList( _serviceProvider.findByFilter( providerFilter, null ), "id", "name",
        		StockConstants.EMPTY_STRING );
        CategoryFilter categoryFilter = new CategoryFilter( );
        categoryFilter.setOrderAsc( true );
        categoryFilter.setOrders( orderList );
        ReferenceList categoryComboList = ListUtils.toReferenceList( _serviceCategory.findByFilter( categoryFilter, null ), "id", "name",
                StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_PROVIDERS, providerComboList );
        model.put( MARK_LIST_CATEGORIES, categoryComboList );
        model.put( "public_list", ListUtils.getPropertyList( "stock-billetterie.show.public" ) );


        model.put( StockConstants.MARK_JSP_BACK, request.getParameter( StockConstants.MARK_JSP_BACK ) );
        model.put( MARK_PRODUCT, product );
        model.put( MARK_URL_POSTER, AppPropertiesService.getProperty( PROPERTY_POSTER_PATH ) );

        if ( product.getId( ) != null && product.getId( ) != 0 )
        {
            model.put( MARK_TITLE, I18nService.getLocalizedString( PAGE_TITLE_MODIFY_PRODUCT, Locale.getDefault( ) ) );
        }
        else
        {
            model.put( MARK_TITLE, I18nService.getLocalizedString( PAGE_TITLE_CREATE_PRODUCT, Locale.getDefault( ) ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SAVE_PRODUCT, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Save a product.
     * 
     * @param request The HTTP request
     * @return redirection url
     */
    public String doSaveProduct( HttpServletRequest request )
    {
        if ( StringUtils.isNotBlank( request.getParameter( StockConstants.PARAMETER_BUTTON_CANCEL ) ) )
        {
            return doGoBack( request );
        }

        ShowDTO product = new ShowDTO( );
        populate( product, request );
        if ( StringUtils.isNotEmpty( request.getParameter( "aLaffiche" ) ) )
        {
            product.setAlaffiche( true );
        }


        try
        {
            // Get and save the poster into temp files
            File[] filePosterArray = writePoster( request, product );

            // Controls mandatory fields
            validate( product );

            // Persist entity
            ShowDTO saveProduct = _serviceProduct.doSaveProduct( product, filePosterArray );

            // Statistic management
            _serviceStatistic.doManageProductSaving( saveProduct );
        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, "SaveProduct.jsp" );
        }

        return doGoBack( request );
    }

    /**
     * Get the poster in the request and write it on the disk.
     * 
     * @param request http request (multipart if contains poster)
     * @param product product entity
     * @return the poster image file (0 : thumbnail, 1 : full size)
     * @throws BusinessException the business exception
     */
    private File[] writePoster( HttpServletRequest request, ShowDTO product ) throws BusinessException
    {
        File[] filePosterArray = null;
        boolean fileGotten = false;
        //File uploaded
        if ( request instanceof MultipartHttpServletRequest )
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            FileItem fileItem = multipartRequest.getFile( PARAMETER_POSTER );
            
            if ( fileItem != null && fileItem.getSize( ) > 0 )
            {
                String posterFolderPath = AppPropertiesService.getProperty( POSTER_FOLDER_PATH );
                if ( StringUtils.isEmpty( posterFolderPath ) )
                {
                    throw new TechnicalException(
                            "Configuration du répertoire de stockage des affiches non trouvées. Veuillez renseigner la propriété "
                                    + POSTER_FOLDER_PATH );
                }
                try
                {
                    InputStream fisPoster = fileItem.getInputStream( );
                    
                    // File fPoster = new File( new File( posterFolderPath ),
                    // fileItem.getName( ) );
                    File fPoster = File.createTempFile( FilenameUtils.getBaseName( fileItem.getName( ) ), null );

                    // Generate unique name
                    fPoster = fr.paris.lutece.plugins.stock.utils.FileUtils.getUniqueFile( fPoster );

                    // Store poster picture
                    fr.paris.lutece.plugins.stock.utils.FileUtils.writeInputStreamToFile( fisPoster, fPoster );

                    // Create a thumbnail image
                    File fTbPoster = ImageUtils.createThumbnail( fPoster,
                            AppPropertiesService.getPropertyInt( PROPERTY_POSTER_WIDTH, 120 ),
                            AppPropertiesService.getPropertyInt( PROPERTY_POSTER_HEIGHT, 200 ) );

                    // Save file name into entity
                    product.setPosterName( fPoster.getName( ) );
                    fileGotten = true;

                    filePosterArray = new File[] { fTbPoster, fPoster };

                }
                catch ( IOException e )
                {
                    throw new TechnicalException( "Problème lors de la récupération de l'affiche", e );
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
     * @param request The Http request
     * @return The url of the last JSP
     */
    private String doGoBack( HttpServletRequest request )
    {
        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

        return StringUtils.isNotBlank( strJspBack ) ? ( AppPathService.getBaseUrl( request ) + strJspBack )
                : AppPathService.getBaseUrl( request ) + JSP_MANAGE_PRODUCTS;
    }

    /**
     * Gets the product filter.
     * 
     * @param request the request
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

        if ( StringUtils.isNotEmpty( request.getParameter( "aLaffiche" ) ) )
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
     * @param request The Http request
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
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
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
        ResultList<SeanceDTO> bookingList = this._serviceOffer.findByFilter( filter, null );

        if ( bookingList != null && !bookingList.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_DELETE_SHOW_WITH_SEANCE, AdminMessage.TYPE_STOP );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DELETE_PRODUCT,
                CATEGORY_DO_DELETE_JSP, AdminMessage.TYPE_CONFIRMATION, urlParam );
    }

    /**
     * Delete a product.
     * 
     * @param request The Http request
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
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }
        //On supprime les statistiques du spectacle
        _serviceStatistic.doRemoveProductStatisticByIdProduct( nIdProduct );

        _serviceProduct.doDeleteProduct( nIdProduct );

        return doGoBack( request );
    }
}
