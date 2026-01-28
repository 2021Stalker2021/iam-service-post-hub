package com.post_hub.iam_service.mapper;

import com.post_hub.iam_service.model.dto.comment.CommentDTO;
import com.post_hub.iam_service.model.entity.Comment;
import com.post_hub.iam_service.model.entity.Post;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.request.comment.CommentRequest;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {DateTimeUtils.class, Object.class}
)
public interface CommentMapper {

    @Mapping(source = "user.id", target = "owner.id")
    @Mapping(source = "user.username", target = "owner.username")
    @Mapping(source = "user.email", target = "owner.email")
    @Mapping(source = "post.id", target = "postId")
    CommentDTO toDto(Comment comment);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "createdBy", source = "user.email")
    Comment toEntity(CommentRequest commentRequest, User user, Post post);
}
