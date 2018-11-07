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
package fr.paris.lutece.plugins.stock.modules.billetterie.service.district;

import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.modules.billetterie.business.district.District;
import fr.paris.lutece.plugins.stock.modules.billetterie.business.district.DistrictFilter;
import fr.paris.lutece.plugins.stock.modules.billetterie.dao.quartier.DistrictDAO;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;

@Service
@Transactional
public class DistrictService
{
    @Inject
    private DistrictDAO _daoQuartier;

    /**
     * Get district by id
     * 
     * @param id
     *            the id of the district search
     * @return the District
     */
    public District findById( Integer id )
    {
        return _daoQuartier.findById( id );
    }

    /**
     * Get all the district in database
     * 
     * @return the list of the district
     */
    public List<District> findAll( )
    {
        return _daoQuartier.findAll( );
    }

    /**
     * Get district by filter
     * 
     * @param filter
     *            the district filter
     * @param pagination
     *            the pagination properties
     * @return the list of the district
     */
    public List<District> findByFilter( DistrictFilter filter, PaginationProperties pagination )
    {
        return _daoQuartier.findByFilter( filter, pagination );
    }
}
