INSERT INTO users (email, name, password, role, username)
VALUES
    ('admin@gmail.com', 'Admin User', '$2a$10$kfxVQdd9Ty0HyaYfE23QW.KK/Jm9k/dIK4F20hW8fo4dco0QHJRs6', 'ADMIN', 'admin'),
    ('user@gmail.com', 'User Test', '$2a$10$YLLEUDRFbPKWDpqwXBHLQOBGmUcFGZcybsJKdNsjnSLvJ.OZizub2', 'USER', 'user')
    ON CONFLICT (username) DO NOTHING;

INSERT INTO movies (name, release_year, synopsis, image, category, created_by, created_at) VALUES
('Inception', 2010, 'A sci-fi thriller exploring dream manipulation and the complexity of the subconscious mind.', NULL, 'Sci-Fi', 1, '2025-04-01'),
('Interstellar', 2014, 'A space odyssey where explorers travel through a wormhole to find a new home for humanity.', NULL, 'Sci-Fi', 1, '2025-04-01'),
('The Dark Knight', 2008, 'Batman faces the anarchic Joker in a battle for Gotham’s soul.', NULL, 'Action', 1, '2025-04-01'),
('Now You See Me', 2013, 'A group of illusionists pull off daring heists during their performances, staying one step ahead of the FBI.', NULL, 'Mystery', 1, '2025-04-01')
    ON CONFLICT (name, release_year) DO NOTHING;;
