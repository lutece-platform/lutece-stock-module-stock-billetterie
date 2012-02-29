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

import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ResultStatistic;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IStatisticService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.StatisticService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.export.TicketsExportUtils;
import fr.paris.lutece.plugins.stock.utils.DateUtils;
import fr.paris.lutece.plugins.stock.utils.constants.StockConstants;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;

import au.com.bytecode.opencsv.CSVWriter;

import com.keypoint.PngEncoder;


/**
 * The Class StatisticJspBean.
 */
public class StatisticJspBean extends AbstractJspBean
{
    public static final String CONSTANT_PRODUCT_TYPE = "1";

    public static final String CONSTANT_GROUP_BY_DAY = "0";
    public static final String CONSTANT_GROUP_BY_WEEK = "1";
    public static final String CONSTANT_GROUP_BY_MONTH = "2";
    public static final String CONSTANT_PURCHASE_TYPE = "2";
    //RIGHT
    public static final String RIGHT_MANAGE_STATISTICS = "STATISTICS_MANAGEMENT";

    //PAGES TITLES
    private static final String PAGE_TITLE_MANAGE_PRODUCTS = "module.stock.billetterie.manage_products_statistics.pageTitle";
    private static final String PAGE_TITLE_MANAGE_PURCHASE = "module.stock.billetterie.manage_purchase_statistics.pageTitle";

    //TEMPLATES
    private static final String TEMPLATE_MANAGE_STATISTICS = "admin/plugins/stock/modules/billetterie/manage_statistics.html";
    private static final String TEMPLATE_MANAGE_PRODUCTS_STATISTICS = "admin/plugins/stock/modules/billetterie/manage_products_statistics.html";
    private static final String TEMPLATE_MANAGE_PURCHASE_STATISTICS = "admin/plugins/stock/modules/billetterie/manage_purchases_statistics.html";

    //MARKS
    private static final String MARK_NUMBER_RESPONSE = "number_response";
    private static final String MARK_FIRST_RESPONSE_DATE_FILTER = "fist_response_date_filter";
    private static final String MARK_LAST_RESPONSE_DATE_FILTER = "last_response_date_filter";

    private static final String PARAMETER_FIRST_RESPONSE_DATE_FILTER = "fist_response_date_filter";
    private static final String PARAMETER_LAST_RESPONSE_DATE_FILTER = "last_response_date_filter";

    private static final String MARK_TIMES_UNIT = "times_unit";
    private static final String PARAMETER_TIMES_UNIT = "times_unit";
    private static final String MARK_LOCALE = "locale";

    private static final String PARAMETER_TYPE_DATA = "type_data";

    //MESSAGES
    private static final String MESSAGE_ERROR_INVALID_DATE = "module.stock.billetterie.message.error.dateBeginBeforeDateEnd";

    //PROPERTIES
    private static final String PROPERTY_PRODUCT_STAT_EXPORT_FILE_NAME = "stock-billetterie.csv.product.file.name";
    private static final String PROPERTY_PURCHASE_STAT_EXPORT_FILE_NAME = "stock-billetterie.csv.purchase.file.name";
	private static final String PROPERTY_ENCODING = "stock-billetterie.csv.encoding";
    private static final String PROPERTY_NUMBER_RESPONSE_AXIS_X = "graph.numberResponseAxisX";
    private static final String PROPERTY_LABEL_AXIS_X = "module.stock.billetterie.manage_statistics.labelAxisX";
    private static final String PROPERTY_LABEL_AXIS_Y_PRODUCT = "module.stock.billetterie.manage_products_statistics.labelAxisY";
    private static final String PROPERTY_LABEL_AXIS_Y_PURCHASE = "module.stock.billetterie.manage_purchases_statistics.labelAxisY";


    /** The _service statistic. */
    // @Inject
    private IStatisticService _serviceStatistic;

    /**
     * Instantiates a new statistic jsp bean.
     */
    public StatisticJspBean(  )
    {
        super(  );
        _serviceStatistic = SpringContextService.getContext( ).getBean( IStatisticService.class );
    }

    /**
     * Generates a HTML form that displays the link to manage the visitors
     * statistics and polls.
     * 
     * @param request the Http request
     * @return HTML
     */
    public String getManageStatistics( HttpServletRequest request )
    {
        setPageTitleProperty( StockConstants.EMPTY_STRING );

        Map<String, Object> model = new HashMap<String, Object>(  );
        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_MANAGE_STATISTICS, getLocale(  ), model );

        return getAdminPage( t.getHtml(  ) );
    }

    /**
     * Gets the form result page.
     * 
     * @param request the http request
     * @return the form test page
     */
    public String getManageProducts( HttpServletRequest request )
    {
        Locale locale = getLocale( );
        HtmlTemplate template;
        int nNumberResponse = 0;
        Map<String, Object> model = new HashMap<String, Object>( );

        String strFistResponseDateFilter = request.getParameter( PARAMETER_FIRST_RESPONSE_DATE_FILTER );
        String strLastResponseDateFilter = request.getParameter( PARAMETER_LAST_RESPONSE_DATE_FILTER );
        String strTimesUnit = request.getParameter( PARAMETER_TIMES_UNIT );
        
        if ( strFistResponseDateFilter != null && strLastResponseDateFilter != null
                && !strFistResponseDateFilter.equals( StringUtils.EMPTY )
                && !strLastResponseDateFilter.equals( StringUtils.EMPTY ) )
        {
            if ( DateUtil.formatDate( strFistResponseDateFilter, locale ).after(
                    DateUtil.formatDate( strLastResponseDateFilter, locale ) ) )
            {
                BusinessException fe = new BusinessException( null, MESSAGE_ERROR_INVALID_DATE );
                model.put( "error", getHtmlError( fe ) );
            }
        }

        Timestamp tFistResponseDateFilter = null;
        Timestamp tLastResponseDateFilter = null;

        if ( strFistResponseDateFilter != null )
        {
            tFistResponseDateFilter = DateUtils.getDateFirstMinute( DateUtil.formatDate( strFistResponseDateFilter,
                    locale ) );
        }

        if ( strLastResponseDateFilter != null )
        {
            tLastResponseDateFilter = DateUtils.getDateLastMinute( DateUtil.formatDate( strLastResponseDateFilter,
                    locale ) );
        }

        nNumberResponse = _serviceStatistic.getCountProductsByDates( strFistResponseDateFilter,
                strLastResponseDateFilter );

        if ( strTimesUnit == null )
        {
            strTimesUnit = CONSTANT_GROUP_BY_DAY;
        }

        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_NUMBER_RESPONSE, nNumberResponse );
        model.put( MARK_FIRST_RESPONSE_DATE_FILTER, ( tFistResponseDateFilter == null ) ? null : new Date(
                tFistResponseDateFilter.getTime( ) ) );
        model.put( MARK_LAST_RESPONSE_DATE_FILTER, ( tLastResponseDateFilter == null ) ? null : new Date(
                tLastResponseDateFilter.getTime( ) ) );
        model.put( MARK_TIMES_UNIT, strTimesUnit );
        //        model.put( MARK_EXPORT_FORMAT_REF_LIST, ExportFormatHome.getListExport( plugin ) );
        setPageTitleProperty( PAGE_TITLE_MANAGE_PRODUCTS );
        template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PRODUCTS_STATISTICS, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Gets the form result page.
     * 
     * @param request the http request
     * @return the form test page
     */
    public String getManagePurchases( HttpServletRequest request )
    {
        Locale locale = getLocale( );
        HtmlTemplate template;
        int nNumberResponse = 0;
        Map<String, Object> model = new HashMap<String, Object>( );

        String strFistResponseDateFilter = request.getParameter( PARAMETER_FIRST_RESPONSE_DATE_FILTER );
        String strLastResponseDateFilter = request.getParameter( PARAMETER_LAST_RESPONSE_DATE_FILTER );
        String strTimesUnit = request.getParameter( PARAMETER_TIMES_UNIT );

        if ( strFistResponseDateFilter != null && strLastResponseDateFilter != null
                && !strFistResponseDateFilter.equals( StringUtils.EMPTY )
                && !strLastResponseDateFilter.equals( StringUtils.EMPTY ) )
        {
            if ( DateUtil.formatDate( strFistResponseDateFilter, locale ).after(
                    DateUtil.formatDate( strLastResponseDateFilter, locale ) ) )
            {
                BusinessException fe = new BusinessException( null, MESSAGE_ERROR_INVALID_DATE );
                model.put( "error", getHtmlError( fe ) );
            }
        }

        Timestamp tFistResponseDateFilter = null;
        Timestamp tLastResponseDateFilter = null;

        if ( strFistResponseDateFilter != null )
        {
            tFistResponseDateFilter = DateUtils.getDateFirstMinute( DateUtil.formatDate( strFistResponseDateFilter,
                    locale ) );
        }

        if ( strLastResponseDateFilter != null )
        {
            tLastResponseDateFilter = DateUtils.getDateLastMinute( DateUtil.formatDate( strLastResponseDateFilter,
                    locale ) );
        }

        nNumberResponse = _serviceStatistic.getCountPurchasesByDates( strFistResponseDateFilter,
                strLastResponseDateFilter );

        if ( strTimesUnit == null )
        {
            strTimesUnit = CONSTANT_GROUP_BY_DAY;
        }

        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_NUMBER_RESPONSE, nNumberResponse );
        model.put( MARK_FIRST_RESPONSE_DATE_FILTER, ( tFistResponseDateFilter == null ) ? null : new Date(
                tFistResponseDateFilter.getTime( ) ) );
        model.put( MARK_LAST_RESPONSE_DATE_FILTER, ( tLastResponseDateFilter == null ) ? null : new Date(
                tLastResponseDateFilter.getTime( ) ) );
        model.put( MARK_TIMES_UNIT, strTimesUnit );
        //model.put( MARK_EXPORT_FORMAT_REF_LIST, ExportFormatHome.getListExport( plugin ) );
        setPageTitleProperty( PAGE_TITLE_MANAGE_PURCHASE );
        template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PURCHASE_STATISTICS, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * write in the http response the statistic graph of all response submit who
     * verify the date filter.
     * 
     * @param request the http request
     * @param response The http response
     */
    public void doGenerateGraph( HttpServletRequest request, HttpServletResponse response )
    {
        Locale locale = getLocale( );
        String strFistResponseDateFilter = request.getParameter( PARAMETER_FIRST_RESPONSE_DATE_FILTER );
        String strLastResponseDateFilter = request.getParameter( PARAMETER_LAST_RESPONSE_DATE_FILTER );
        String strTimesUnit = request.getParameter( PARAMETER_TIMES_UNIT );
        String strTypeData = request.getParameter( PARAMETER_TYPE_DATA );

        List<ResultStatistic> listeResultStatistic = null;

        if ( strTypeData.equals( CONSTANT_PRODUCT_TYPE ) )
        {

            listeResultStatistic = _serviceStatistic.getProductStatistic( strTimesUnit, strFistResponseDateFilter,
                    strLastResponseDateFilter );
        }
        else
        {
            listeResultStatistic = _serviceStatistic.getPurchaseStatistic( strTimesUnit, strFistResponseDateFilter,
                    strLastResponseDateFilter );
        }

        String strNumberOfResponseAxisX = AppPropertiesService.getProperty( PROPERTY_NUMBER_RESPONSE_AXIS_X );
        int nNumberOfResponseAxisX = 10;

        try
        {
            nNumberOfResponseAxisX = Integer.parseInt( strNumberOfResponseAxisX );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        List<ResultStatistic> listStatisticGraph = new ArrayList<ResultStatistic>( );
        ResultStatistic statisticFormSubmit;

        if ( listeResultStatistic.size( ) != 0 )
        {
            for ( int cpt = 0; cpt < nNumberOfResponseAxisX; cpt++ )
            {
                statisticFormSubmit = new ResultStatistic( );
                statisticFormSubmit.setNumberResponse( 0 );
                statisticFormSubmit.setStatisticDate( StatisticService.addStatisticInterval(
                        listeResultStatistic.get( 0 )
                        .getStatisticDate( ), strTimesUnit, cpt ) );
                listStatisticGraph.add( statisticFormSubmit );
            }
        }

        for ( ResultStatistic statisticFormSubmitGraph : listStatisticGraph )
        {
            for ( ResultStatistic resultStatistic : listeResultStatistic )
            {
                if ( StatisticService.sameDate( statisticFormSubmitGraph.getStatisticDate( ),
                        resultStatistic.getStatisticDate( ), strTimesUnit ) )
                {
                    statisticFormSubmitGraph.setNumberResponse( resultStatistic.getNumberResponse( ) );
                }
            }
        }

        String strLabelAxisX = I18nService.getLocalizedString( PROPERTY_LABEL_AXIS_X, locale );
        String strLabelAxisY;
        if ( strTypeData.equals( CONSTANT_PRODUCT_TYPE ) )
        {

            strLabelAxisY = I18nService.getLocalizedString( PROPERTY_LABEL_AXIS_Y_PRODUCT, locale );
        }
        else
        {
            strLabelAxisY = I18nService.getLocalizedString( PROPERTY_LABEL_AXIS_Y_PURCHASE, locale );
        }

        try
        {
            JFreeChart chart = StatisticService.createXYGraph( listStatisticGraph, strLabelAxisX, strLabelAxisY,
                    strTimesUnit );

            ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection( ) );
            BufferedImage chartImage = chart.createBufferedImage( 600, 200, info );
            response.setContentType( "image/PNG" );

            PngEncoder encoder = new PngEncoder( chartImage, false, 0, 9 );
            response.getOutputStream( ).write( encoder.pngEncode( ) );
            response.getOutputStream( ).close( );
        }
        catch ( Exception e )
        {
            AppLogService.error( e );
        }
    }

    /**
     * Exports all the statistics visitors filtered.
     * 
     * @param request The Http request
     * @param response The Http response
     * @return The Url after the export
     */
    public String doExportStatistics( HttpServletRequest request, HttpServletResponse response )
    {
        String strFistResponseDateFilter = request.getParameter( PARAMETER_FIRST_RESPONSE_DATE_FILTER );
        String strLastResponseDateFilter = request.getParameter( PARAMETER_LAST_RESPONSE_DATE_FILTER );
        String strTimesUnit = request.getParameter( PARAMETER_TIMES_UNIT );
        String strTypeData = request.getParameter( PARAMETER_TYPE_DATA );
        List<ResultStatistic> listeResultStatistic = null;
        String strExportFileName;

        if ( strTypeData.equals( CONSTANT_PRODUCT_TYPE ) )
        {
            listeResultStatistic = _serviceStatistic.getProductStatistic( strTimesUnit, strFistResponseDateFilter,
                    strLastResponseDateFilter );
            strExportFileName = PROPERTY_PRODUCT_STAT_EXPORT_FILE_NAME;
        }
        else
        {
            listeResultStatistic = _serviceStatistic.getPurchaseStatistic( strTimesUnit, strFistResponseDateFilter,
                    strLastResponseDateFilter );
            strExportFileName = PROPERTY_PURCHASE_STAT_EXPORT_FILE_NAME;
        }

        List<String[]> listToCSVWriter = StatisticService.buildListToCSVWriter( listeResultStatistic, strTimesUnit,
                getLocale( ) );

        //        if ( listToCSVWriter == null )
        //        {
        //            if ( strTypeData.equals( CONSTANT_PRODUCT_TYPE ) )
        //            {
        //                return getManageProducts( request );
        //            }
        //            else
        //            {
        //                return getManagePurchases( request );
        //            }
        //        }

        String strCsvSeparator = AppPropertiesService.getProperty( TicketsConstants.PROPERTY_CSV_SEPARATOR );
        StringWriter strWriter = new StringWriter(  );
        CSVWriter csvWriter = new CSVWriter( strWriter, strCsvSeparator.toCharArray(  )[0] );

        csvWriter.writeAll( listToCSVWriter );

        String strEncoding = AppPropertiesService.getProperty( PROPERTY_ENCODING );
        byte[] byteFileOutPut;

        try
        {
            byteFileOutPut = strWriter.toString(  ).getBytes( strEncoding );
        }
        catch ( UnsupportedEncodingException e )
        {
            byteFileOutPut = strWriter.toString(  ).getBytes(  );
        }

        try
        {
            String strFormatExtension = AppPropertiesService.getProperty( TicketsConstants.PROPERTY_CSV_EXTENSION );
            String strFileName = AppPropertiesService.getProperty( strExportFileName ) + "."
                    +
                strFormatExtension;
            TicketsExportUtils.addHeaderResponse( request, response, strFileName, strFormatExtension );
            response.setContentLength( (int) byteFileOutPut.length );

            OutputStream os = response.getOutputStream(  );
            os.write( byteFileOutPut );
            os.close(  );

            csvWriter.close(  );
            strWriter.close(  );
        }
        catch ( IOException e )
        {
            AppLogService.error( e );
        }

        UrlItem url = new UrlItem( getManageProducts( request ) );

        return url.getUrl(  );
    }
}
