package com.server.ssh;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * A simple SSH server implementation using Apache MINA SSHD.
 * This server provides basic SSH functionality with password authentication.
 */
public class SSHServer {
    private static final Logger logger = LoggerFactory.getLogger(SSHServer.class);
    
    private static final int DEFAULT_PORT = 2222;
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";
    
    private final SshServer sshd;
    private final int port;

    /**
     * Creates a new SSH server instance with default settings.
     */
    public SSHServer() {
        this(DEFAULT_PORT);
    }

    /**
     * Creates a new SSH server instance on the specified port.
     *
     * @param port the port number to bind the SSH server to
     */
    public SSHServer(int port) {
        this.port = port;
        this.sshd = SshServer.setUpDefaultServer();
        configureServer();
    }

    /**
     * Configures the SSH server with authentication and key provider.
     */
    private void configureServer() {
        sshd.setPort(port);
        
        // Set up host key provider - generates RSA key pair if not exists
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(
            Paths.get("hostkey.ser")));
        
        // Configure password authentication
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String username, String password, ServerSession session) {
                logger.info("Authentication attempt - Username: {}", username);
                boolean authenticated = DEFAULT_USERNAME.equals(username) && 
                                      DEFAULT_PASSWORD.equals(password);
                if (authenticated) {
                    logger.info("Authentication successful for user: {}", username);
                } else {
                    logger.warn("Authentication failed for user: {}", username);
                }
                return authenticated;
            }
        });
        
        // Set up shell factory - provides shell command execution
        // This creates a subprocess shell based on the OS
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            sshd.setShellFactory(new ProcessShellFactory("cmd.exe"));
        } else {
            sshd.setShellFactory(new ProcessShellFactory("/bin/sh", "-i", "-l"));
        }
        
        logger.info("SSH server configured on port {}", port);
    }

    /**
     * Starts the SSH server.
     *
     * @throws IOException if an I/O error occurs while starting the server
     */
    public void start() throws IOException {
        sshd.start();
        logger.info("SSH server started successfully on port {}", port);
        logger.info("Connect using: ssh {}@localhost -p {}", DEFAULT_USERNAME, port);
        logger.info("Password: {}", DEFAULT_PASSWORD);
    }

    /**
     * Stops the SSH server.
     *
     * @throws IOException if an I/O error occurs while stopping the server
     */
    public void stop() throws IOException {
        sshd.stop();
        logger.info("SSH server stopped");
    }

    /**
     * Main entry point for the SSH server application.
     *
     * @param args command line arguments (optional: port number)
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        // Parse command line arguments
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1 || port > 65535) {
                    logger.error("Invalid port number: {}. Using default port {}", port, DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid port argument: {}. Using default port {}", args[0], DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        try {
            SSHServer server = new SSHServer(port);
            server.start();
            
            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutdown signal received, stopping SSH server...");
                try {
                    server.stop();
                } catch (IOException e) {
                    logger.error("Error stopping SSH server", e);
                }
            }));
            
            logger.info("SSH server is running. Press Ctrl+C to stop.");
            
            // Keep the server running
            Thread.currentThread().join();
            
        } catch (IOException e) {
            logger.error("Failed to start SSH server", e);
            System.exit(1);
        } catch (InterruptedException e) {
            logger.info("SSH server interrupted");
            Thread.currentThread().interrupt();
        }
    }
}
