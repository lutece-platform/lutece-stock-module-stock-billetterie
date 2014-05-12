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
package fr.paris.lutece.plugins.stock.modules.billetterie.dao.quartier;

import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.dao.AbstractStockDAO;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.modules.billetterie.business.district.District;
import fr.paris.lutece.plugins.stock.modules.billetterie.business.district.DistrictFilter;
import fr.paris.lutece.plugins.stock.service.StockPlugin;

import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;


/**
 *
 * @author jchaline
 *
 */
@Repository
public class DistrictDAO extends AbstractStockDAO<Integer, District>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName(  )
    {
        return StockPlugin.PLUGIN_NAME;
    }

    /**
     * Find purchases by filter.
     *
     * @param filter the filter
     * @param paginationProperties the pagination properties
     * @return list of purchases
     */
    public ResultList<District> findByFilter( DistrictFilter filter, PaginationProperties paginationProperties )
    {
        EntityManager em = getEM(  );
        CriteriaBuilder cb = em.getCriteriaBuilder(  );

        CriteriaQuery<District> cq = cb.createQuery( District.class );

        Root<District> root = cq.from( District.class );
        buildCriteriaQuery( filter, root, cq, cb );
        buildSortQuery( filter, root, cq, cb );
        cq.distinct( true );

        return createPagedQuery( cq, paginationProperties ).getResultList(  );
    }

    /**
     * Add the order by parameter to the query.
     *
     * @param filter the filter
     * @param root the entity root
     * @param query the criteria query
     * @param builder the criteria builder
     */
    protected void buildSortQuery( DistrictFilter filter, Root<District> root, CriteriaQuery<District> query,
        CriteriaBuilder builder )
    {
        if ( ( filter.getOrders(  ) != null ) && !filter.getOrders(  ).isEmpty(  ) )
        {
            List<Order> orderList = new LinkedList<Order>(  );

            // get asc order
            for ( String order : filter.getOrders(  ) )
            {
                if ( filter.isOrderAsc(  ) )
                {
                    orderList.add( builder.asc( root.get( order ) ) );
                }
                else
                {
                    orderList.add( builder.desc( root.get( order ) ) );
                }
            }

            query.orderBy( orderList );
        }
    }

    /**
     * Build the criteria query used when entity are searched by filter.
     *
     * @param filter the filter
     * @param root the entity root
     * @param query the criteria query
     * @param builder the criteria builder
     */
    protected void buildCriteriaQuery( Object filter, Root<District> root, CriteriaQuery<District> query,
        CriteriaBuilder builder )
    {
    }
}
