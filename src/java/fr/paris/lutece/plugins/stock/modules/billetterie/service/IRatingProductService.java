package fr.paris.lutece.plugins.stock.modules.billetterie.service;

import fr.paris.lutece.plugins.stock.modules.billetterie.business.RatingProductDTO;

import java.util.List;

public interface IRatingProductService {

    List<RatingProductDTO> getAllRatingProduct();
}
