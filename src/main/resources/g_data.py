import random
from datetime import datetime, timedelta
import bcrypt

password_plain = ""
hashed_password = bcrypt.hashpw(password_plain.encode(), bcrypt.gensalt()).decode()

with open("data.sql", "w") as f:
    # Users
    for i in range(1, 501):
        username = f"user{i}"
        handle = f"user{i}"
        f.write(f"INSERT INTO users (id, username, handle, password) VALUES ('{username}', '{username}', '{handle}', '{hashed_password}');\n")

    # Posts
    for i in range(1, 10001):
        user_id = f"user{random.randint(1, 500)}"
        date_posted = (datetime.now() - timedelta(days=random.randint(0, 365))).strftime('%Y-%m-%d')
        content = f"Post content {i} - Lorem ipsum dolor sit amet."
        f.write(f"INSERT INTO posts (id, post, date_posted, views, user_id) VALUES ('post{i}', '{content}', '{date_posted}', {random.randint(0, 1000)}, '{user_id}');\n")

    # Follows
    for i in range(1, 2001):
        follower = f"user{random.randint(1, 500)}"
        followed = f"user{random.randint(1, 500)}"
        if follower != followed:
            f.write(f"INSERT INTO follows (id, follower_user_id, followed_user_id) VALUES ('follow{i}', '{follower}', '{followed}');\n")

    # Likes
    for i in range(1, 5001):
        user = f"user{random.randint(1, 500)}"
        post = f"post{random.randint(1, 10000)}"
        f.write(f"INSERT INTO likes (id, post_id, user_id) VALUES ('like{i}', '{post}', '{user}');\n")