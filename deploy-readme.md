# API Deployment - Universi.me
## Dependencies
- `Docker`, `docker-compose`
- `Nginx` (or any Reverse Proxy)

## Environment Variables
Environment variables are stored in `.env` file located in root directory of Project. You can edit `.env` and fill in the environment variables. 

### API Environment Variables
- `API_DB_NAME`: Database name (default: `universime_api`)
- `API_DB_USER`: Database user
- `API_DB_PASS`: Database password
- `API_PROFILE_ACTIVE`: Active profile (Default: `dev`, values:` prod, dev, test`)
- `API_LOCAL_ORGANIZATION_ID_ENABLED`: Enable local organization ID
- `API_LOCAL_ORGANIZATION_ID`: Local organization group ID
- `API_MINIO_ENABLED`: Enable Minio (default: `false`)
- `API_MINIO_URL`: Minio URL (Minio server https url)
- `API_MINIO_ACESSKEY`: Minio access key
- `API_MINIO_SECRET`: Minio secret key
- `API_MINIO_BUCKET`: Minio bucket name (default: `universime`)

### Minio Environment Variables
- `MINIO_ROOT_USER`: Minio root user
- `MINIO_ROOT_PASSWORD`: Minio root password
- `MINIO_SERVER_URL`: Minio server URL (requires https and specific domain)
- `MINIO_BROWSER_REDIRECT_URL`: Minio browser redirect URL (for Minio Console)

### PgAdmin Environment Variables
- `PGADMIN_DEFAULT_PASSWORD`: PgAdmin default password
- `PGADMIN_DEFAULT_EMAIL`: PgAdmin default email

## Deployment
1. Clone this repository, go to the root directory of Project
2. Edit `.env` configuring and filling in the environment variables
3. Run `docker-compose build --no-cache`
4. Run `docker-compose stop` (if already running)
5. Run `docker-compose up -d`

