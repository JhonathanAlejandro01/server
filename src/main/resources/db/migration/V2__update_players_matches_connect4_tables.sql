-- Actualización del esquema: ajustes en tablas players, matches y connect4_moves

ALTER TABLE players
ADD COLUMN games_played INT NOT NULL DEFAULT 0 AFTER password,
ADD COLUMN games_winner INT NOT NULL DEFAULT 0 AFTER games_played,
ADD COLUMN games_tied INT NOT NULL DEFAULT 0 AFTER games_winner,
ADD COLUMN online_state TINYINT NOT NULL DEFAULT 0 AFTER games_tied;

ALTER TABLE matches
ADD COLUMN player1_id BIGINT NOT NULL AFTER game_id,
ADD COLUMN player2_id BIGINT NULL AFTER player1_id,
ADD CONSTRAINT fk_matches_player1 FOREIGN KEY (player1_id) REFERENCES players(id),
ADD CONSTRAINT fk_matches_player2 FOREIGN KEY (player2_id) REFERENCES players(id);

ALTER TABLE connect4_moves
DROP COLUMN symbol,
ADD COLUMN color CHAR(1) NOT NULL COMMENT 'R = Red, Y = Yellow' AFTER row_played;

DROP TABLE IF EXISTS player_matches;
