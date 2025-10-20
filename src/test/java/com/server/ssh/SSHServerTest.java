package com.server.ssh;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Basic test for SSH server functionality.
 */
public class SSHServerTest {

    private SSHServer server;
    private static final int TEST_PORT = 3333;

    @Before
    public void setUp() {
        server = new SSHServer(TEST_PORT);
    }

    @After
    public void tearDown() throws IOException {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testServerCreation() {
        assertNotNull("Server should be created", server);
    }

    @Test
    public void testServerStartAndStop() throws IOException {
        server.start();
        // If we get here without exception, start was successful
        server.stop();
        // If we get here without exception, stop was successful
    }
}
