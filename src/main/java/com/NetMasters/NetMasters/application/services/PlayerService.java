package com.NetMasters.NetMasters.application.services;

import com.NetMasters.NetMasters.core.entities.Player;
import com.NetMasters.NetMasters.core.events.PlayerLoggedInEvent;
import com.NetMasters.NetMasters.core.events.PlayerLoggedOutEvent;
import com.NetMasters.NetMasters.core.interfaces.EventBus;
import com.NetMasters.NetMasters.core.interfaces.PlayerServiceInterface;
import com.NetMasters.NetMasters.core.valueobjects.PlayerId;
import com.NetMasters.NetMasters.infrastructure.persistence.models.PlayerModel;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.PlayerRepository;
import com.NetMasters.NetMasters.presentation.mappers.PlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService implements PlayerServiceInterface {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final PasswordEncoder passwordEncoder;
    private final EventBus eventBus;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PlayerMapper playerMapper, PasswordEncoder passwordEncoder, EventBus eventBus) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventBus = eventBus;
    }

    @Transactional
    public Player createPlayer(Player player) {
        if (playerRepository.findByEmail(player.getEmail()).isPresent()){
            //modificar por una excepcion personalizada para duplicados
            throw new RuntimeException("Email already exists: " + player.getEmail());
        }
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        player.setOnlineState(true); // Nuevo usuario se registra como online
        PlayerModel playerModel = playerMapper.playerToPlayerModel(player);
        return playerMapper.playerModelToPlayer(playerRepository.save(playerModel));
    }

    @Transactional(readOnly = true)
    public List<Player> getAllPlayers() {
        return playerMapper.playerModelsToPlayers(playerRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id).map(playerMapper::playerModelToPlayer);
    }

    @Transactional
    public Optional<Player> updatePlayer(Long id, Player player) {
        return playerRepository.findById(id).map(existingPlayer -> {
            player.setId(id);
            PlayerModel updatedPlayerModel = playerMapper.playerToPlayerModel(player);
            return playerMapper.playerModelToPlayer(playerRepository.save(updatedPlayerModel));
        });
    }

    @Transactional
    public boolean deletePlayer(Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Implementaciones de UserService interface
    @Override
    public Player register(String username, String email, String password) {
        return createPlayer(Player.builder()
                .username(username)
                .email(email)
                .password(password)
                .build());
    }

    @Override
    public Optional<Player> authenticate(String email, String password) {
        return authenticatePlayer(email, password);
    }

    @Override
    public void logoutByEmail(String email) {
        logoutPlayer(email);
    }

    @Override
    public void logoutByUsername(String username) {
        logoutPlayerByUsername(username);
    }

    @Override
    public List<Player> getOnlineUsers() {
        return getAllPlayers().stream()
                .filter(Player::isOnlineState)
                .toList();
    }

    @Override
    public Optional<Player> getPlayerByEmail(String email) {
        return playerRepository.findByEmail(email)
                .map(playerMapper::playerModelToPlayer);
    }

    @Transactional
    public Optional<Player> authenticatePlayer(String email, String password) {
        Optional<PlayerModel> playerModelOpt = playerRepository.findByEmail(email);
        if (playerModelOpt.isPresent()) {
            PlayerModel playerModel = playerModelOpt.get();
            if (passwordEncoder.matches(password, playerModel.getPassword())) {
                // Al hacer login, marcar como online directamente en el modelo
                playerModel.setOnlineState(true);
                playerRepository.save(playerModel);
                Player player = playerMapper.playerModelToPlayer(playerModel);

                // Publicar evento de login
                eventBus.publish(new PlayerLoggedInEvent(this, new PlayerId(player.getId()), player.getUsername()));

                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public void logoutPlayer(String email) {
        Optional<PlayerModel> playerModelOpt = playerRepository.findByEmail(email);
        if (playerModelOpt.isPresent()) {
            PlayerModel playerModel = playerModelOpt.get();
            String username = playerModel.getUsername();
            playerModel.setOnlineState(false);
            playerRepository.save(playerModel);

            // Publicar evento de logout
            eventBus.publish(new PlayerLoggedOutEvent(this, new PlayerId(playerModel.getId()), username));
        }
    }

    @Transactional
    public void logoutPlayerByUsername(String username) {
        // Buscar por username en lugar de email
        List<PlayerModel> playerModels = playerRepository.findAll();
        for (PlayerModel playerModel : playerModels) {
            if (playerModel.getUsername().equals(username)) {
                playerModel.setOnlineState(false);
                playerRepository.save(playerModel);

                // Publicar evento de logout
                eventBus.publish(new PlayerLoggedOutEvent(this, new PlayerId(playerModel.getId()), username));
                break;
            }
        }
    }
}
