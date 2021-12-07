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

import fr.paris.lutece.plugins.stock.modules.billetterie.business.RatingProductDTO;
import fr.paris.lutece.plugins.stock.modules.billetterie.service.IRatingProductService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IStatisticService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static java.util.Optional.ofNullable;

/**
 *
 * DatabaseAdminDashboardComponent
 *
 */
public class BilleterieDashboardComponent extends DashboardComponent
{
    private static final String MARK_NB_PURCHASE_COUNT_OF_MONTH = "nbPurchaseCountDuMois";
    private static final String MARK_NB_PURCHASE_COUNT_OF_DAY = "nbPurchaseCountDuJour";
    private static final String MARK_NB_PRODUCT_A_VENIR = "nbProductAVenir";
    private static final String MARK_NB_PRODUCT_A_L_AFFICHE = "nbProductALAffiche";
    private static final String MARK_LIST_RATING_FOR_PRODUCT = "list_product_with_rating_note";
    private static final String MARK_NUMBER_OF_ITEM_PER_PAGE = "nItem_per_page";
    private static final String MARK_SIZE_LIST_RATING_PRODUCT = "size_list_rp";

    public static final String BEAN_RATING_PRODUCT_SERVICE = "stock-billetterie.RatingProductService";

    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String MARK_PAGINATOR = "paginator";

    // defaults
    private static final String DEFAULT_PAGE_INDEX = "1";

    // TEMPLATES
    private static final String TEMPLATE_ADMIN_DASHBOARD = "admin/plugins/stock/modules/billetterie/billeterie_dashboard.html";

    // Variables
    private IRatingProductService _ratingProductService = SpringContextService.getBean( BEAN_RATING_PRODUCT_SERVICE );

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        HtmlTemplate template;
        IStatisticService statisticService = SpringContextService.getContext( ).getBean( IStatisticService.class );

        Integer nProductCountALAffiche = statisticService.getCountProductALAffiche( );
        Integer nProductCountAVenir = statisticService.getCountProductAVenir( );

        Integer nbPurchaseCountOfDay = statisticService.getCountPurchaseOfDay( );
        Integer nbPurchaseCountOfMonth = statisticService.getCountPurchaseOfMonth( );

        List<RatingProductDTO> ratingProductDTOList = new ArrayList<>( );
        ratingProductDTOList = _ratingProductService.getAllRatingProduct( );

        Paginator<RatingProductDTO> paginator = null;
        int nItemPerPage = 5;

        if ( ratingProductDTOList != null )
        {
            ratingProductDTOList.sort( Comparator.comparing( RatingProductDTO::getRating ).reversed( ) );

            UrlItem urlItem = new UrlItem( AppPathService.getAdminMenuUrl( ) );
            String strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, DEFAULT_PAGE_INDEX );

            paginator = new Paginator<>( ratingProductDTOList, nItemPerPage, urlItem.getUrl( ), PARAMETER_PAGE_INDEX, strCurrentPageIndex );
        }

        // Fill the model
        Map<String, Object> model = new HashMap<>( );

        model.put( MARK_NB_PRODUCT_A_L_AFFICHE, nProductCountALAffiche );
        model.put( MARK_NB_PRODUCT_A_VENIR, nProductCountAVenir );

        model.put( MARK_NB_PURCHASE_COUNT_OF_DAY, nbPurchaseCountOfDay );
        model.put( MARK_NB_PURCHASE_COUNT_OF_MONTH, nbPurchaseCountOfMonth );

        model.put( MARK_PAGINATOR, paginator );

        if ( paginator != null )
        {
            model.put( MARK_LIST_RATING_FOR_PRODUCT, paginator.getPageItems( ) );
        }

        model.put( MARK_NUMBER_OF_ITEM_PER_PAGE, nItemPerPage );
        model.put( MARK_SIZE_LIST_RATING_PRODUCT, ofNullable( ratingProductDTOList ).map( List::size ).orElse( 0 ) );

        template = AppTemplateService.getTemplate( TEMPLATE_ADMIN_DASHBOARD, user.getLocale( ), model );

        return template.getHtml( );
    }
}
