package oreumi.group2.carrotClone.service;

import oreumi.group2.carrotClone.model.Post;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    List<Post> searchPostsByTitle (String keyword);
    List<Post> searchPostsByLocation (String location);
    List<Post> getPostsSortedByNewest ();
    List<Post> getPostsSortedByPrice (boolean ascending);
    Post getPostById (Long id);
    Post createPost (Post post);

}
