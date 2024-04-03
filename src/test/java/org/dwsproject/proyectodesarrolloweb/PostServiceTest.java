package org.dwsproject.proyectodesarrolloweb;

import org.dwsproject.proyectodesarrolloweb.Classes.Post;
import org.dwsproject.proyectodesarrolloweb.Repositories.PostRepository;
import org.dwsproject.proyectodesarrolloweb.Service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    public void testEditById() {
        // Arrange
        Post post = new Post();
        post.setText("<script>alert('XSS')</script>");
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        postService.editById(post, 1L);

        // Assert
        verify(postRepository, times(1)).save(any(Post.class));
        assertNotEquals("<script>alert('XSS')</script>", post.getText());
    }
}
