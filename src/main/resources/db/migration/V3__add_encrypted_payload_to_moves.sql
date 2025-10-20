-- Add encrypted payload columns to move tables

ALTER TABLE triqui_moves
ADD COLUMN encrypted_payload TEXT NULL AFTER move_time;

ALTER TABLE connect4_moves
ADD COLUMN encrypted_payload TEXT NULL AFTER move_time;
