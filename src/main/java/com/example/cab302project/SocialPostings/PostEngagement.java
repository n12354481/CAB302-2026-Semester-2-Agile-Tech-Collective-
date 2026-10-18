package com.example.cab302project.SocialPostings;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class PostEngagement {

    private static final Map<Integer, Set<Integer>> registeredMockPostIds = new HashMap<>();
    private static final Map<Integer, Set<Integer>> interestedMockPostIds = new HashMap<>();

    private final ISocialPostingsDAO dao;
    
    public PostEngagement(ISocialPostingsDAO dao) {
        this.dao = dao;
    }

    public Set<Integer> getRegisteredMockIds(int userId) {
        return registeredMockPostIds.computeIfAbsent(userId, id -> new HashSet<>());
    }

    public Set<Integer> getInterestedMockIds(int userId) {
        return interestedMockPostIds.computeIfAbsent(userId, id -> new HashSet<>());
    }

    public boolean isRegistered(int userId, SocialPostings post) {
        if (post.postId() < 0) {
            return getRegisteredMockIds(userId).contains(post.postId());
        }
        try {
            return dao.isRegistered(userId, post.postId());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleRegistration(int userId, SocialPostings post) {
        boolean nowRegistered = !isRegistered(userId, post);
        try {
            if (post.postId() < 0) {
                updateMock(getRegisteredMockIds(userId), post.postId(), nowRegistered);
            } else if (nowRegistered) {
                dao.registerForPost(userId, post.postId());
            } else {
                dao.unregisterFromPost(userId, post.postId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return !nowRegistered;
        }
        return nowRegistered;
    }

    public boolean isInterested(int userId, SocialPostings post) {
        if (post.postId() < 0) {
            return getInterestedMockIds(userId).contains(post.postId());
        }
        try {
            return dao.isInterested(userId, post.postId());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleInterest(int userId, SocialPostings post) {
        boolean nowInterested = !isInterested(userId, post);
        try {
            if (post.postId() < 0) {
                updateMock(getInterestedMockIds(userId), post.postId(), nowInterested);
            } else if (nowInterested) {
                dao.markInterested(userId, post.postId());
            } else {
                dao.removeInterest(userId, post.postId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return !nowInterested;
        }
        return nowInterested;
    }

    private void updateMock(Set<Integer> ids, int postId, boolean add) {
        if (add) {
            ids.add(postId);
        } else {
            ids.remove(postId);
        }
    }
}
