package com.post_hub.iam_service.model.request.comment;

import com.post_hub.iam_service.model.enums.CommentSortField;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommentSearchRequest implements Serializable {

    private String message;
    private String createdBy;
    private Integer postId;

    private Boolean deleted;
    private String keyword;
    private CommentSortField sortField;

}
