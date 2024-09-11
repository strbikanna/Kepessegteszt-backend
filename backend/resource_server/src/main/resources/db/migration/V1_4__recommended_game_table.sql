CREATE TABLE recommended_game (
                                  id                 bigint          PRIMARY KEY AUTO_INCREMENT,
                                  _timestamp         datetime,
                                  config             json,
                                  _level             int,
                                  recommender_id     int             ,
                                  recommendee_id     int             NOT NULL,
                                  game_id            int             NOT NULL,
                                  completed          boolean         DEFAULT false,
                                  constraint fk_recommender FOREIGN KEY(recommender_id) REFERENCES user(id),
                                  constraint fk_recommendee FOREIGN KEY(recommendee_id) REFERENCES user(id),
                                  constraint fk_game_recommended FOREIGN KEY(game_id) REFERENCES game(id)
);
