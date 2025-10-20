package com.NetMasters.NetMasters.tools;

import com.NetMasters.NetMasters.infrastructure.persistence.config.AesEncryptionService;
import com.NetMasters.NetMasters.infrastructure.persistence.models.ConnectFourMoveModel;
import com.NetMasters.NetMasters.infrastructure.persistence.models.TriquiMoveModel;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.ConnectFourMoveRepository;
import com.NetMasters.NetMasters.infrastructure.persistence.repositories.TriquiMoveRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MoveDecryptRunner implements ApplicationRunner {

    private final AesEncryptionService encryptionService;
    private final TriquiMoveRepository triquiMoveRepository;
    private final ConnectFourMoveRepository connectFourMoveRepository;
    

    public MoveDecryptRunner(AesEncryptionService encryptionService,
                             TriquiMoveRepository triquiMoveRepository,
                             ConnectFourMoveRepository connectFourMoveRepository) {
        this.encryptionService = encryptionService;
        this.triquiMoveRepository = triquiMoveRepository;
        this.connectFourMoveRepository = connectFourMoveRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Check if CLI mode requested
        if (!args.containsOption("decrypt.type") || !args.containsOption("decrypt.id")) {
            return; // not CLI invocation
        }

        String type = args.getOptionValues("decrypt.type").get(0);
        String idStr = args.getOptionValues("decrypt.id").get(0);
        Long id = Long.parseLong(idStr);

        System.out.println("Attempting to decrypt move: type=" + type + " id=" + id);

        if ("triqui".equalsIgnoreCase(type)) {
            Optional<TriquiMoveModel> moveOpt = triquiMoveRepository.findById(id);
            if (moveOpt.isEmpty()) {
                System.err.println("Triqui move not found: " + id);
                System.exit(2);
            }
            TriquiMoveModel move = moveOpt.get();
            String cipher = move.getEncryptedPayload();
            if (cipher == null) {
                System.err.println("No encrypted payload for move " + id);
                System.exit(3);
            }
            String plain = encryptionService.decrypt(cipher);
            System.out.println("Decrypted payload:\n" + plain);
            System.exit(0);
        } else if ("connect4".equalsIgnoreCase(type) || "connectfour".equalsIgnoreCase(type)) {
            Optional<ConnectFourMoveModel> moveOpt = connectFourMoveRepository.findById(id);
            if (moveOpt.isEmpty()) {
                System.err.println("Connect4 move not found: " + id);
                System.exit(2);
            }
            ConnectFourMoveModel move = moveOpt.get();
            String cipher = move.getEncryptedPayload();
            if (cipher == null) {
                System.err.println("No encrypted payload for move " + id);
                System.exit(3);
            }
            String plain = encryptionService.decrypt(cipher);
            System.out.println("Decrypted payload:\n" + plain);
            System.exit(0);
        } else {
            System.err.println("Unknown type: " + type);
            System.exit(4);
        }
    }
}
