CREATE TABLE games (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name ENUM('TRIQUI', 'CONNECT4') NOT NULL,
  genre VARCHAR(100),
  release_date DATE
);

CREATE TABLE players (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE matches (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  game_id BIGINT NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NULL,
  status ENUM('PENDING','IN_PROGRESS','FINISHED','CANCELLED') NOT NULL,
  FOREIGN KEY (game_id) REFERENCES games(id)
);

CREATE TABLE player_matches (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  player_id BIGINT NOT NULL,
  match_id BIGINT NOT NULL,
  score INT,
  joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (player_id) REFERENCES players(id),
  FOREIGN KEY (match_id) REFERENCES matches(id)
);

CREATE TABLE rankings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  player_id BIGINT NOT NULL,
  total_score INT NOT NULL DEFAULT 0,
  player_rank INT,
  FOREIGN KEY (player_id) REFERENCES players(id)
);

CREATE TABLE triqui_games (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  match_id BIGINT NOT NULL,
  board VARCHAR(9) NOT NULL,
  current_turn BIGINT NOT NULL,
  winner BIGINT NULL,
  FOREIGN KEY (match_id) REFERENCES matches(id),
  FOREIGN KEY (current_turn) REFERENCES players(id),
  FOREIGN KEY (winner) REFERENCES players(id)
);

CREATE TABLE triqui_moves (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  triqui_game_id BIGINT NOT NULL,
  player_id BIGINT NOT NULL,
  move_number INT NOT NULL,
  position INT NOT NULL,
  symbol CHAR(1) NOT NULL,
  move_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (triqui_game_id) REFERENCES triqui_games(id),
  FOREIGN KEY (player_id) REFERENCES players(id)
);

CREATE TABLE connect4_games (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  match_id BIGINT NOT NULL,
  row_connect4 INT NOT NULL DEFAULT 6,
  column_connect4 INT NOT NULL DEFAULT 7,
  board TEXT NOT NULL,
  current_turn BIGINT NOT NULL,
  winner BIGINT NULL,
  FOREIGN KEY (match_id) REFERENCES matches(id),
  FOREIGN KEY (current_turn) REFERENCES players(id),
  FOREIGN KEY (winner) REFERENCES players(id)
);

CREATE TABLE connect4_moves (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  connect4_game_id BIGINT NOT NULL,
  player_id BIGINT NOT NULL,
  move_number INT NOT NULL,
  column_played INT NOT NULL,
  row_played INT NOT NULL,
  symbol CHAR(1) NOT NULL,
  move_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (connect4_game_id) REFERENCES connect4_games(id),
  FOREIGN KEY (player_id) REFERENCES players(id)
);