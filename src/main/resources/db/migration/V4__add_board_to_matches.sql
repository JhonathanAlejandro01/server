-- Add board column to matches to store serialized board JSON
ALTER TABLE matches
ADD COLUMN board TEXT;
