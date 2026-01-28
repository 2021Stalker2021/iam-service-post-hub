package com.post_hub.iam_service.service;

import com.post_hub.iam_service.model.dto.comment.CommentDTO;
import com.post_hub.iam_service.model.request.comment.CommentRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import jakarta.validation.constraints.NotNull;

public interface CommentService {

    IamResponse<CommentDTO> getCommentById(@NotNull Integer commentId);

    IamResponse<CommentDTO> createComment(@NotNull CommentRequest request);

}
