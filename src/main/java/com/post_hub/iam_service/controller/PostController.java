package com.post_hub.iam_service.controller;

import com.post_hub.iam_service.model.constants.ApiLogMessage;
import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.dto.post.PostSearchDTO;
import com.post_hub.iam_service.model.request.post.NewPostRequest;
import com.post_hub.iam_service.model.request.post.PostSearchRequest;
import com.post_hub.iam_service.model.request.post.UpdatePostRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.model.response.PaginationResponse;
import com.post_hub.iam_service.service.PostService;
import com.post_hub.iam_service.utils.ApiUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated // для валидации входных параметров
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<IamResponse<PostDTO>> getPostById(@PathVariable(name = "id") Integer postId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<PostDTO> response = postService.getById(postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<IamResponse<PostDTO>> createPost(@RequestBody @Valid NewPostRequest newPostRequest) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        // TODO: replace 1 with the real user_id
        Long userId = 1L;

        IamResponse<PostDTO> response = postService.createPost(userId, newPostRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IamResponse<PostDTO>> updatePostById(@PathVariable(name = "id") Integer postId,
                                                               @RequestBody @Valid UpdatePostRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<PostDTO> updatedPost = postService.updatePost(postId, request);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeletePostById(@PathVariable(name = "id") Integer postId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        postService.softDeletePost(postId);
        return ResponseEntity.ok().build(); // 200 OK
    }

    @GetMapping("/all")
    public ResponseEntity<IamResponse<PaginationResponse<PostSearchDTO>>> getAllPosts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        IamResponse<PaginationResponse<PostSearchDTO>> response = postService.findAllPosts(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<IamResponse<PaginationResponse<PostSearchDTO>>> searchPosts(
            @RequestBody @Valid PostSearchRequest postSearchDTO,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());


        Pageable pageable = PageRequest.of(page, limit);
        IamResponse<PaginationResponse<PostSearchDTO>> response = postService.searchPosts(postSearchDTO, pageable);
        return ResponseEntity.ok(response);
    }

}
