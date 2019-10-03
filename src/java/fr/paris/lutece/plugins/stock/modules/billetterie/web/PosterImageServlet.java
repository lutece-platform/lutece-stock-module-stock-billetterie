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

import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used for special solr queries
 *
 * @author abataille
 */
public class PosterImageServlet extends HttpServlet
{
    private static final String ERROR_MESSAGE = "Appel de PosterImageServlet avec un id de produit null";
    private static final String CONTENT_TYPE_IMAGE_JPEG = "image/jpeg";
    private static final String PARAMETER_TB = "tb";
    private static final String PARAMETER_REAL_IMAGE = "img_real";
    private static final String PARAMETER_PRODUCT_ID = "product_id";
    private static final String BEAN_STOCK_TICKETS_SHOW_SERVICE = "stock-tickets.showService";

    /**
     *
     */
    private static final long serialVersionUID = -1165966480942742905L;
    private static final Logger LOGGER = Logger.getLogger( PosterImageServlet.class );

    /** The product service. */
    private IShowService _productService = (IShowService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SHOW_SERVICE );

    /**
     * Returns poster image
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        String sIdProduct = request.getParameter( PARAMETER_PRODUCT_ID );

        if ( StringUtils.isNotEmpty( sIdProduct ) )
        {
            Integer idProduct = Integer.parseInt( sIdProduct );
            boolean isThumbnail = ( request.getParameter( PARAMETER_TB ) != null ) && request.getParameter( PARAMETER_TB ).equals( String.valueOf( true ) );
            boolean isImageReal = ( request.getParameter( PARAMETER_REAL_IMAGE ) != null )
                    && request.getParameter( PARAMETER_REAL_IMAGE ).equals( String.valueOf( true ) );
            byte [ ] bImage;

            if ( isImageReal )
            {
                bImage = _productService.getRealImage( idProduct );
            }
            else
            {
                if ( isThumbnail )
                {
                    bImage = _productService.getTbImage( idProduct );
                }
                else
                {
                    bImage = _productService.getImage( idProduct );
                }
            }
            response.setContentLength( bImage.length );
            response.setContentType( CONTENT_TYPE_IMAGE_JPEG );

            ServletOutputStream os = response.getOutputStream( );
            IOUtils.write( bImage, os );
            os.flush( );
            os.close( );
        }
        else
        {
            LOGGER.error( ERROR_MESSAGE );
        }
    }
}
