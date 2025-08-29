package com.post_hub.iam_service.mapper;

import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.entity.Post;
import com.post_hub.iam_service.model.request.post.PostRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        // Стратегия IGNORE позволит сохранить исходное знач-е поля если мы передали null
        imports = {DateTimeUtils.class, Object.class}
)
public interface PostMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "likes", target = "likes")
    @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    PostDTO toPostDTO(Post post);

    @Mapping(target = "id", ignore = true) // игнорируем поле т.к. оно будет сгенерировано базой данных
    @Mapping(target = "created", ignore = true) // игнорируем поле т.к. оно будет сгенерировано базой данных
    Post createPost(PostRequest postRequest);
}
