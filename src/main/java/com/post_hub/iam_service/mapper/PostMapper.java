package com.post_hub.iam_service.mapper;

import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.dto.post.PostSearchDTO;
import com.post_hub.iam_service.model.entity.Post;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.request.post.NewPostRequest;
import com.post_hub.iam_service.model.request.post.UpdatePostRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        // Стратегия IGNORE позволит сохранить исходное знач-е поля если мы передали null
        imports = {DateTimeUtils.class, Object.class}
)
public interface PostMapper {


    PostDTO toPostDTO(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(source = "user", target = "user")
    @Mapping(source = "createdBy", target = "createdBy")
    Post createPost(NewPostRequest newPostRequest, User user, String createdBy);

    @Mapping(target = "id", ignore = true) // игнорируем поле т.к. оно будет сгенерировано базой данных
    @Mapping(target = "created", ignore = true) // игнорируем поле т.к. оно будет сгенерировано базой данных
    void updatePost(@MappingTarget Post post, UpdatePostRequest request);


    @Mapping(source = "deleted", target = "isDeleted")
    @Mapping(target = "createdBy", source = "user.username") // поле в сущности Post
    PostSearchDTO toPostSearchDTO(Post post);
}
