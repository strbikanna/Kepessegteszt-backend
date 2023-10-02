CREATE TABLE RECOMMENDED_GAMES (
                                  id                 int             PRIMARY KEY,
                                  time_stamp         datetime,
                                  recommender_id     int             NOT NULL,
                                  recommendee_id     int             NOT NULL,
                                  game_id            int             NOT NULL,
                                  constraint fk_recommender FOREIGN KEY(recommender_id) REFERENCES user(id),
                                  constraint fk_recommendee FOREIGN KEY(recommendee_id) REFERENCES user(id),
                                  constraint fk_game_2 FOREIGN KEY(game_id) REFERENCES games(id)
);