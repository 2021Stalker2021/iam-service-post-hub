package com.post_hub.iam_service.service.impl;

import com.post_hub.iam_service.kafka.service.KafkaMessageService;
import com.post_hub.iam_service.mapper.PostMapper;
import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.dto.post.PostSearchDTO;
import com.post_hub.iam_service.model.entity.Post;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.DataExistException;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.model.request.post.NewPostRequest;
import com.post_hub.iam_service.model.request.post.PostSearchRequest;
import com.post_hub.iam_service.model.request.post.UpdatePostRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.model.response.PaginationResponse;
import com.post_hub.iam_service.repository.PostRepository;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.repository.criteria.PostSearchCriteria;
import com.post_hub.iam_service.security.validation.AccessValidator;
import com.post_hub.iam_service.service.PostService;
import com.post_hub.iam_service.utils.ApiUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final AccessValidator accessValidator;
    private final ApiUtils apiUtils;
    private final KafkaMessageService kafkaMessageService;

    @Override
    public IamResponse<PostDTO> getById(@NotNull Integer postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        PostDTO postDTO = postMapper.toPostDTO(post);

        return IamResponse.createSuccessful(postDTO);
    }

    @Override
    public IamResponse<PostDTO> createPost(@NotNull NewPostRequest postRequest) {
        if (postRepository.existsByTitle(postRequest.getTitle())) { // если пост существует выбрасываем exception
            throw new DataExistException(ApiErrorMessage.POST_ALREADY_EXISTS.getMessage(postRequest.getTitle()));
        }

        Long userId = apiUtils.getUserIdFromAuthentication();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(ApiErrorMessage.USERNAME_NOT_FOUND.getMessage(userId)));

        Post post = postMapper.createPost(postRequest, user, user.getUsername());
        post = postRepository.save(post);

        kafkaMessageService.sendPostCreatedMessage(user.getId(), post.getId()); // KAFKA

        return IamResponse.createSuccessful(postMapper.toPostDTO(post));
    }

    @Override
    public IamResponse<PostDTO> updatePost(@NotNull Integer postId, @NotNull UpdatePostRequest request) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        accessValidator.validateAdminOrOwnerAccess(post.getUser().getId());

        if (postRepository.existsByTitle(request.getTitle())) { // если пост существует выбрасываем exception
            throw new DataExistException(ApiErrorMessage.POST_ALREADY_EXISTS.getMessage(request.getTitle()));
        }

        postMapper.updatePost(post, request);
        post.setUpdated(LocalDateTime.now());
        post = postRepository.save(post);

        kafkaMessageService.sendPostUpdatedMessage(post.getUser().getId(),  post.getId()); // KAFKA

        PostDTO postDTO = postMapper.toPostDTO(post);
        return IamResponse.createSuccessful(postDTO);
    }

    @Override
    public void softDeletePost(Integer postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));

        accessValidator.validateAdminOrOwnerAccess(post.getUser().getId());

        post.setDeleted(true);
        postRepository.save(post);

        kafkaMessageService.sendPostDeletedMessage(post.getUser().getId(), post.getId()); // KAFKA
    }

    @Override
    public IamResponse<PaginationResponse<PostSearchDTO>> findAllPosts(Pageable pageable) {
        Page<PostSearchDTO> posts = postRepository.findAll(pageable)
                .map(postMapper::toPostSearchDTO);

        PaginationResponse<PostSearchDTO> paginationResponse = new PaginationResponse<>(
                posts.getContent(),
                new PaginationResponse.Pagination(
                        posts.getTotalElements(), // Общее количество элементов во всех страницах
                        pageable.getPageSize(), // Количество элементов на одной странице (размер страницы)
                        pageable.getPageNumber() + 1, // Текущий номер страницы (нумерация с 1)
                        posts.getTotalPages() // Общее количество страниц
                )
        );

        return IamResponse.createSuccessful(paginationResponse);
    }

    @Override
    public IamResponse<PaginationResponse<PostSearchDTO>> searchPosts(PostSearchRequest request, Pageable pageable) {
        Specification<Post> specification = new PostSearchCriteria(request);
        Page<PostSearchDTO> posts = postRepository.findAll(specification, pageable)
                .map(postMapper::toPostSearchDTO);

        PaginationResponse<PostSearchDTO> response = PaginationResponse.<PostSearchDTO>builder()
                .content(posts.getContent())
                .pagination(PaginationResponse.Pagination.builder()
                        .total(posts.getTotalElements())
                        .limit(pageable.getPageSize())
                        .page(posts.getNumber() + 1)
                        .pages(posts.getTotalPages())
                        .build())
                .build();

        return IamResponse.createSuccessful(response);
    }
}
