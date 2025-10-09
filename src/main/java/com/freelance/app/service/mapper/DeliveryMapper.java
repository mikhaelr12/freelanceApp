package com.freelance.app.service.mapper;

import com.freelance.app.domain.Delivery;
import com.freelance.app.domain.FileObject;
import com.freelance.app.domain.Order;
import com.freelance.app.service.dto.DeliveryDTO;
import com.freelance.app.service.dto.FileObjectDTO;
import com.freelance.app.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Delivery} and its DTO {@link DeliveryDTO}.
 */
@Mapper(componentModel = "spring")
public interface DeliveryMapper extends EntityMapper<DeliveryDTO, Delivery> {
    //    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    //    @Mapping(target = "file", source = "file", qualifiedByName = "fileObjectObjectKey")
    //    DeliveryDTO toDto(Delivery s);
    //
    //    @Named("orderId")
    //    @BeanMapping(ignoreByDefault = true)
    //    @Mapping(target = "id", source = "id")
    //    OrderDTO toDtoOrderId(Order order);
    //
    //    @Named("fileObjectObjectKey")
    //    @BeanMapping(ignoreByDefault = true)
    //    @Mapping(target = "id", source = "id")
    //    @Mapping(target = "objectKey", source = "objectKey")
    //    FileObjectDTO toDtoFileObjectObjectKey(FileObject fileObject);
}
