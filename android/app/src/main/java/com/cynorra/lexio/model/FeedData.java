package com.cynorra.lexio.model;

import java.util.ArrayList;
import java.util.List;

public class FeedData {
    private static final List<FeedPost> POSTS = new ArrayList<>();

    public static List<FeedPost> getPosts() {
        return POSTS;
    }

    public static void setPosts(List<FeedPost> newPosts) {
        POSTS.clear();
        if (newPosts != null) {
            POSTS.addAll(newPosts);
        }
    }

    public static void addPost(FeedPost post) {
        if (post != null) {
            POSTS.add(0, post);
        }
    }
}

