package com.freelance.app.service.mapper;

import com.freelance.app.domain.FavoriteOffer;
import com.freelance.app.domain.OfferPackage;
import com.freelance.app.domain.Order;
import com.freelance.app.domain.User;
import com.freelance.app.service.dto.FavoriteOfferDTO;
import com.freelance.app.service.dto.OfferPackageDTO;
import com.freelance.app.service.dto.OrderDTO;
import com.freelance.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "buyer", source = "buyer", qualifiedByName = "userLogin")
    @Mapping(target = "seller", source = "seller", qualifiedByName = "userLogin")
    @Mapping(target = "offerpackage", source = "offerpackage", qualifiedByName = "offerPackageId")
    OrderDTO toDto(Order s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("offerPackageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OfferPackageDTO toDtoOfferPackageId(OfferPackage offerPackage);

    @BeanMapping(ignoreByDefault = true)
    Order toEntity(OrderDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, ignoreByDefault = true)
    void partialUpdate(@MappingTarget Order entity, OrderDTO dto);
}
