package com.post_hub.iam_service.service;

import com.post_hub.iam_service.mapper.PostMapper;
import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.entity.Post;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.NotFoundException;
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
}
