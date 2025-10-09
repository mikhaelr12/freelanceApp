package com.freelance.app.service.mapper;

import com.freelance.app.domain.FileObject;
import com.freelance.app.service.dto.FileObjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FileObject} and its DTO {@link FileObjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface FileObjectMapper extends EntityMapper<FileObjectDTO, FileObject> {}
