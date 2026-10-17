package com.post_hub.iam_service.service;

import com.post_hub.iam_service.mapper.PostMapper;
import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.entity.Post;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.model.request.post.NewPostRequest;
import com.post_hub.iam_service.repository.PostRepository;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.service.impl.PostServiceImpl;
import com.post_hub.iam_service.utils.ApiUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private ApiUtils apiUtils;

    @InjectMocks
    private PostServiceImpl postService;

    private Post testPost;
    private PostDTO testPostDTO;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("TestUser");

        testPost = new Post();
        testPost.setId(1);
        testPost.setTitle("Test Post");
        testPost.setContent("Test Content");
        testPost.setUser(testUser);

        testPostDTO = new PostDTO();
        testPostDTO.setId(1);
        testPostDTO.setTitle("Test Post");
        testPostDTO.setContent("Test Content");
    }

    @Test
    void getById_PostExists_ReturnPostDTO() {
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testPost));
        when(postMapper.toPostDTO(testPost)).thenReturn(testPostDTO);

        PostDTO result = postService.getById(1).getPayload();

        assertNotNull(result);
        assertEquals(testPostDTO.getId(), result.getId());
        assertEquals(testPostDTO.getTitle(), result.getTitle());

        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(postMapper, times(1)).toPostDTO(testPost);

    }

    @Test
    void getById_PostNotExists_ThrowsException() {
        when(postRepository.findByIdAndDeletedFalse(999)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> postService.getById(999));

        assertTrue(exception.getMessage().contains("not found"));

        verify(postRepository, times(1)).findByIdAndDeletedFalse(999);
        verify(postMapper, never()).toPostDTO(any(Post.class));
    }

    @Test
    void createPost_OK() {
        NewPostRequest request = new NewPostRequest("New Title", "New Content", 100);

        when(apiUtils.getUserIdFromAuthentication()).thenReturn(testUser.getId());
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(postMapper.createPost(request, testUser, testUser.getUsername())).thenReturn(testPost);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(postMapper.toPostDTO(testPost)).thenReturn(testPostDTO);

        PostDTO result = postService.createPost(request).getPayload();

        assertNotNull(result);
        assertEquals(testPostDTO.getId(), result.getId());
        assertEquals(testPostDTO.getContent(), result.getContent());

        verify(apiUtils, times(1)).getUserIdFromAuthentication();
        verify(userRepository, times(1)).findById(testUser.getId());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postMapper, times(1)).toPostDTO(any(Post.class));
    }
}
