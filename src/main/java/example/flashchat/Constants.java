package example.flashchat;

public class Constants {
    public static final String UPLOAD_DIR = "/uploads/";

    public static final double LIKE_WEIGHT = 0.35;
    public static final double COMMENT_WEIGHT = 0.5;
    public static final double VIEW_WEIGHT = 0.15;

    public static final double DIRECT_MESSAGE_WEIGHT = 0.15;
    
    public static final double DECAY_FACTOR = 0.5;

    public static final double MEDIA_WEIGHT = 0.35;
    public static final double POST_LENGTH_WEIGHT = 0.35;
    public static final double NOT_REPORTED_WEIGHT = 0.3;

    public static final double FOLLOW_MULTIPLIER = 0.4;
    public static final double INTERACTIVITY_MULTIPLIER = 0.3;
    public static final double RECENCY_MULTIPLIER = 0.1;
    public static final double POPULARITY_MULTIPLIER = 0.15;
    public static final double CONTENT_MULTIPLIER = 0.05;
}
