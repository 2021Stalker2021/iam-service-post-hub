package com.post_hub.iam_service.service;

import com.post_hub.iam_service.mapper.PostMapper;
import com.post_hub.iam_service.model.dto.post.PostDTO;
import com.post_hub.iam_service.model.entity.Post;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.repository.PostRepository;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.utils.ApiUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private PostService postService;

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
}
