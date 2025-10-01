package com.post_hub.iam_service.mapper;

import com.post_hub.iam_service.model.dto.user.UserDto;
import com.post_hub.iam_service.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring", // Интегрирует маппер с Spring Framework.
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(source = "last_login", target = "lastLogin")
    UserDto toUserDTO(User user);

}
