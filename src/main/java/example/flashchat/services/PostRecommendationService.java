package example.flashchat.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.flashchat.Constants;
import example.flashchat.models.Post;
import example.flashchat.models.User;
import example.flashchat.repositories.PostRepo;

@Service
public class PostRecommendationService {

    @Autowired
    private PostRepo postRepo;

    public List<Post> getRecommendedPosts(User user) {

        // Get all posts
        List<Post> allPosts = postRepo.findAll();

        // Calculate scores for each post.
        // Sort posts by score.
        Map<Post, Double> postScores = allPosts.stream()
            .collect(Collectors.toMap(post -> post, post -> calculatePostScores(post, user)))
            .entrySet().stream()
            .sorted(Map.Entry.<Post, Double>comparingByValue().reversed())
            .collect(Collectors.toMap(postEntry -> postEntry.getKey(), postEntry -> postEntry.getValue())); 

        // Return the sorted posts based on scores.
        allPosts = postScores.keySet().stream().collect(Collectors.toList());
        
        return allPosts;
    }

    private double calculatePostScores(Post post, User user) {
        // Calculate the scores.
        double followScore = calculateFollowScore(post, user);
        double popularityScore = calculatePopularityScore(post);
        double interactivityScore = calculateInteractivityScore(post, user);
        double recentScore = calculateRecentScore(post);
        double contentScore = calculateContentScore(post);

        // Combine the scores using weights.
        double totalScore = (followScore * Constants.FOLLOW_MULTIPLIER) +
                            (popularityScore * Constants.POPULARITY_MULTIPLIER) +
                            (interactivityScore * Constants.INTERACTIVITY_MULTIPLIER) +
                            (recentScore * Constants.RECENCY_MULTIPLIER) +
                            (contentScore * Constants.CONTENT_MULTIPLIER);

        return totalScore;
    }

    private double calculateFollowScore(Post post, User user) {
        // Check the author of the post is followed by the user.
        if (user.getFollowing().stream().anyMatch(follow -> follow.getFollowed().getId().equals(post.getUser().getId()))) {
            return 1.0;
        }

        // Check that author of the post is a follower of the user.
        if (user.getFollowers().stream().anyMatch(follower -> follower.getFollower().getId().equals(post.getUser().getId()))) {
            return 0.7;
        }
    


        // Check if the user follows someone who follows the author of the post
        boolean friendOfFriend = user.getFollowing().stream().anyMatch(follow ->
            follow.getFollowed().getFollowing().stream()
                .anyMatch(secondFollow -> secondFollow.getFollowed().getId().equals(post.getUser().getId()))
        );

        if (friendOfFriend) {
            return 0.5;
        }

        return 0.1;
    }

    private double calculatePopularityScore(Post post) {
        // Popularity score relative to decaying over time.
        
        int likes = post.getLikes().size();
        int comments = post.getComments().size();
        int views = post.getViews();

        double likeScore = likes * Constants.LIKE_WEIGHT;
        double commentScore = comments * Constants.COMMENT_WEIGHT;
        double viewScore = views * Constants.VIEW_WEIGHT;

        double baseScore = likeScore + commentScore + viewScore;

        long daysSincePost = ChronoUnit.DAYS.between(
            post.getDatePosted(),
            Instant.now().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
        );

        return baseScore * Math.exp(-daysSincePost * Constants.DECAY_FACTOR);
    }

    private double calculateInteractivityScore(Post post, User user) {
        double interactivityScore = 0.0;

        // Check whether the user has liked the post from this author before.
        // Check if the post is not the same as the post being checked.
        if (user.getLikedPosts().stream().anyMatch(like -> like.getPostLiked().getId() != post.getId() && like.getPostLiked().getId().equals(post.getId()))) {
            interactivityScore += Constants.LIKE_WEIGHT;
        }

        // check whether the user has commented on the post from this author before.
        // Check if the post is not the same as the post being checked.
        if (user.getComments().stream().anyMatch(comment -> comment.getPost().getId() != post.getId() && comment.getPost().getId().equals(post.getId()))) {
            interactivityScore += Constants.COMMENT_WEIGHT;
        }
        
        // check whether the user has a direct message with the author of the post.
        if (user.getMessages().stream().anyMatch(dm -> dm.getSender().getId().equals(post.getUser().getId()) || dm.getSender().getId().equals(post.getUser().getId()))) {
            interactivityScore += Constants.DIRECT_MESSAGE_WEIGHT;
        }

        return interactivityScore;
    }

    private double calculateRecentScore(Post post) {
        long hoursSincePost = ChronoUnit.HOURS.between(
            post.getDatePosted(),
            Instant.now().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
        );

        double recencyScore = 0.0;

        if (hoursSincePost < 1) {
            recencyScore = 1.0;
        } 
        else if (hoursSincePost < 24) {
            recencyScore = 0.8;
        } 
        else if (hoursSincePost < 168) {
            recencyScore = 0.6;
        }
        else if (hoursSincePost < 720) {
            recencyScore = 0.4;
        }
        else {
            recencyScore = 0.2;
        }

        return recencyScore;
    }

    private double calculateContentScore(Post post) {
        double contentScore = 0.0;

        if (post.getMedia().size() > 0) {
            contentScore += Constants.MEDIA_WEIGHT;
        }

        if (post.getPost().length() > 100) {
            contentScore += Constants.POST_LENGTH_WEIGHT;
        }

        if (post.getReports().size() == 0) {
            contentScore += Constants.NOT_REPORTED_WEIGHT;
        }

        return contentScore;
    }
}