# Java SSH Server

A simple and secure SSH server implementation in Java using Apache MINA SSHD framework. This server provides basic SSH functionality with password authentication and shell command execution capabilities.

## Features

- **SSH Protocol Support**: Full SSH protocol implementation via Apache MINA SSHD
- **Password Authentication**: Secure password-based authentication
- **Shell Access**: Interactive shell command execution
- **Cross-Platform**: Works on Windows, Linux, and macOS
- **Configurable Port**: Customize the SSH server port
- **Automatic Host Key Generation**: RSA host keys are automatically generated
- **Logging**: Comprehensive logging with SLF4J

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Building the Project

1. Clone the repository:
```bash
git clone https://github.com/JhonathanAlejandro01/server.git
cd server
```

2. Build the project with Maven:
```bash
mvn clean package
```

This will create an executable JAR file in the `target/` directory.

## Running the Server

### Using Maven

Run the server directly with Maven:
```bash
mvn exec:java -Dexec.mainClass="com.server.ssh.SSHServer"
```

### Using the JAR file

Run the server using the built JAR:
```bash
java -jar target/java-ssh-server-1.0.0.jar
```

### Custom Port

Specify a custom port (default is 2222):
```bash
java -jar target/java-ssh-server-1.0.0.jar 3333
```

## Connecting to the Server

Once the server is running, connect using any SSH client:

```bash
ssh admin@localhost -p 2222
```

**Default Credentials:**
- Username: `admin`
- Password: `admin123`

### Example Session

```bash
$ ssh admin@localhost -p 2222
admin@localhost's password: admin123

# You now have shell access
$ ls
$ pwd
$ echo "Hello from SSH server"
```

## Configuration

### Default Settings

- **Port**: 2222
- **Username**: admin
- **Password**: admin123
- **Host Key File**: hostkey.ser (automatically generated)

### Customizing Authentication

To customize the authentication credentials, modify the following constants in `SSHServer.java`:

```java
private static final String DEFAULT_USERNAME = "admin";
private static final String DEFAULT_PASSWORD = "admin123";
```

## Project Structure

```
server/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── server/
│       │           └── ssh/
│       │               └── SSHServer.java
│       └── resources/
│           └── simplelogger.properties
├── pom.xml
├── .gitignore
└── README.md
```

## Dependencies

- **Apache MINA SSHD Core** (2.10.0): SSH protocol implementation
- **SLF4J API** (2.0.9): Logging facade
- **SLF4J Simple** (2.0.9): Simple logging implementation

## Security Considerations

⚠️ **Warning**: This is a basic implementation intended for development and testing purposes.

For production use, consider:
- Implementing public key authentication instead of password authentication
- Using stronger passwords or external authentication systems
- Restricting available commands and shell access
- Implementing rate limiting and connection throttling
- Adding IP whitelisting/blacklisting
- Using TLS/SSL certificates
- Regular security audits

## Troubleshooting

### Port Already in Use

If port 2222 is already in use, specify a different port:
```bash
java -jar target/java-ssh-server-1.0.0.jar 3333
```

### Permission Denied (Port < 1024)

On Unix-like systems, ports below 1024 require root privileges:
```bash
sudo java -jar target/java-ssh-server-1.0.0.jar 22
```

### Connection Refused

Ensure:
1. The server is running
2. No firewall is blocking the port
3. You're using the correct port number

## License

This project is open source and available under the MIT License.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Author

JhonathanAlejandro01