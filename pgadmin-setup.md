# pgAdmin Setup Guide

This guide will help you connect to your PostgreSQL databases using pgAdmin in Docker.

## Understanding the Port Configuration

pgAdmin runs on port 80 inside its container by default. In our Docker Compose configuration, we've mapped this internal port 80 to port 5050 on your host machine. This is defined in the docker-compose.yml file:

```yaml
pgadmin:
  # other configuration...
  ports:
    - "5050:80"  # Maps host port 5050 to container port 80
```

Port 5050 was chosen because:
1. It avoids conflicts with common ports (80, 8080, etc.) that might be used by other services
2. It's easy to remember and not typically used by other applications
3. You can change it to any available port on your system if needed

## Accessing pgAdmin

1. Start your Docker Compose environment:
   ```
   docker-compose up -d
   ```

2. Open your browser and navigate to:
   ```
   http://localhost:5050
   ```

3. Log in with the following credentials:
   - Email: `admin@example.com`
   - Password: `admin`

## Connecting to Databases

### Connecting to User Database

1. Right-click on "Servers" in the left panel
2. Choose "Register" > "Server"
3. In the "General" tab:
   - Name: `User Database` (or any name you prefer)
4. In the "Connection" tab:
   - Host name/address: `user-db` (use the service name from docker-compose)
   - Port: `5432`
   - Maintenance database: `User`
   - Username: Your database username (from SPRING_DATASOURCE_USERNAME)
   - Password: Your database password (from SPRING_DATASOURCE_PASSWORD)
5. Click "Save"

### Connecting to Form Database

1. Right-click on "Servers" again
2. Choose "Register" > "Server"
3. In the "General" tab:
   - Name: `Form Database` (or any name you prefer)
4. In the "Connection" tab:
   - Host name/address: `form-db` (use the service name from docker-compose)
   - Port: `5432` (inside Docker network, use the container's port)
   - Maintenance database: `postgres`
   - Username: Your database username (from SPRING_DATASOURCE_USERNAME)
   - Password: Your database password (from SPRING_DATASOURCE_PASSWORD)
5. Click "Save"

## Important Notes

1. **Docker Network**: When connecting to your PostgreSQL containers from pgAdmin within Docker, use the service names as hostnames (not localhost), as they're all on the same Docker network.

2. **Port Mapping**: Even though form-db is mapped to port 5433 on your host, within the Docker network it's still accessible on port 5432 (its internal port).

3. **Persistence**: The volume mapping for pgAdmin ensures your server connections persist even after restarting containers.

4. **Security**: For production environments, change the default pgAdmin password in the docker-compose.yml file.

5. **Troubleshooting Connection Issues**:
   - Verify network connectivity with: `docker-compose exec pgadmin ping user-db`
   - Check logs with: `docker-compose logs pgadmin`
   - Ensure PostgreSQL is accepting connections: `docker-compose exec user-db pg_isready`