# REMEDY-BACK Helm Chart

Helm chart for deploying REMEDY-BACK Spring Boot application with PostgreSQL, MongoDB, and Redis.

## Prerequisites

- Kubernetes 1.19+
- Helm 3.0+
- PV provisioner support in the underlying infrastructure (for persistent volumes)

## Installing the Chart

### Basic Installation

```bash
# From the project root directory
helm install remedy ./helm/remedy-back
```

### Installation with Custom Values

```bash
# Using a custom values file
helm install remedy ./helm/remedy-back -f custom-values.yaml

# Or override specific values
helm install remedy ./helm/remedy-back \
  --set app.image.repository=your-docker-registry/remedy-back \
  --set app.image.tag=v1.0.0 \
  --set postgresql.password=secure-password
```

## Uninstalling the Chart

```bash
helm uninstall remedy
```

This removes all Kubernetes resources associated with the chart.

## Configuration

The following table lists the configurable parameters and their default values.

### Application Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `app.name` | Application name | `remedy-app` |
| `app.replicaCount` | Number of replicas | `2` |
| `app.image.repository` | Docker image repository | `YOUR_DOCKER_IMAGE` |
| `app.image.tag` | Docker image tag | `latest` |
| `app.image.pullPolicy` | Image pull policy | `Always` |
| `app.service.type` | Kubernetes service type | `ClusterIP` |
| `app.service.port` | Service port | `8080` |
| `app.resources.requests.memory` | Memory request | `512Mi` |
| `app.resources.requests.cpu` | CPU request | `500m` |
| `app.resources.limits.memory` | Memory limit | `1Gi` |
| `app.resources.limits.cpu` | CPU limit | `1000m` |

### PostgreSQL Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `postgresql.enabled` | Enable PostgreSQL | `true` |
| `postgresql.image.tag` | PostgreSQL image tag | `17` |
| `postgresql.persistence.enabled` | Enable persistence | `true` |
| `postgresql.persistence.size` | PVC size | `10Gi` |
| `postgresql.database` | Database name | `remedy` |
| `postgresql.username` | Database username | `remedy` |
| `postgresql.password` | Database password | `change-this-password` |

### MongoDB Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `mongodb.enabled` | Enable MongoDB | `true` |
| `mongodb.image.tag` | MongoDB image tag | `8.0` |
| `mongodb.persistence.enabled` | Enable persistence | `true` |
| `mongodb.persistence.size` | PVC size | `10Gi` |
| `mongodb.database` | Database name | `remedy` |

### Redis Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `redis.enabled` | Enable Redis | `true` |
| `redis.image.tag` | Redis image tag | `7-alpine` |
| `redis.persistence.enabled` | Enable persistence | `true` |
| `redis.persistence.size` | PVC size | `5Gi` |

### AWS Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `aws.s3.bucket` | S3 bucket name | `your-s3-bucket-name` |
| `aws.s3.region` | AWS region | `ap-northeast-2` |
| `aws.s3.mp3Bucket` | MP3 bucket name | `your-mp3-bucket-name` |
| `aws.s3.hlsBucket` | HLS bucket name | `your-hls-bucket-name` |
| `aws.accessKey` | AWS access key | `your-aws-access-key` |
| `aws.secretKey` | AWS secret key | `your-aws-secret-key` |

### External API Configuration

| Parameter | Description | Default |
|-----------|-------------|---------|
| `externalAPIs.youtube.apiKey` | YouTube API key | `your-youtube-api-key` |
| `externalAPIs.spotify.clientId` | Spotify client ID | `your-spotify-client-id` |
| `externalAPIs.spotify.clientSecret` | Spotify client secret | `your-spotify-client-secret` |

## Usage Examples

### Development Environment

```bash
helm install remedy-dev ./helm/remedy-back \
  --set app.replicaCount=1 \
  --set postgresql.persistence.size=5Gi \
  --set mongodb.persistence.size=5Gi
```

### Production Environment

Create a `production-values.yaml` file:

```yaml
app:
  replicaCount: 3
  image:
    repository: your-registry/remedy-back
    tag: "v1.0.0"
  resources:
    requests:
      memory: "1Gi"
      cpu: "1000m"
    limits:
      memory: "2Gi"
      cpu: "2000m"

postgresql:
  persistence:
    size: 50Gi
    storageClass: "fast-ssd"
  password: "strong-production-password"

mongodb:
  persistence:
    size: 50Gi
    storageClass: "fast-ssd"

redis:
  persistence:
    size: 10Gi
    storageClass: "fast-ssd"

aws:
  s3:
    bucket: "production-remedy-bucket"
    mp3Bucket: "production-mp3-bucket"
    hlsBucket: "production-hls-bucket"
  accessKey: "PRODUCTION_ACCESS_KEY"
  secretKey: "PRODUCTION_SECRET_KEY"

jwt:
  secretKey: "production-jwt-secret-key-very-long-and-random"

externalAPIs:
  youtube:
    apiKey: "production-youtube-key"
  spotify:
    clientId: "production-spotify-id"
    clientSecret: "production-spotify-secret"
```

Then install:

```bash
helm install remedy-prod ./helm/remedy-back -f production-values.yaml
```

### Disable Databases (Use External Services)

```bash
helm install remedy ./helm/remedy-back \
  --set postgresql.enabled=false \
  --set mongodb.enabled=false \
  --set redis.enabled=false
```

Update ConfigMap values to point to external database URLs.

## Upgrading

```bash
# Upgrade with new values
helm upgrade remedy ./helm/remedy-back -f updated-values.yaml

# Upgrade with specific parameters
helm upgrade remedy ./helm/remedy-back \
  --set app.image.tag=v2.0.0
```

## Rollback

```bash
# Rollback to previous release
helm rollback remedy

# Rollback to specific revision
helm rollback remedy 1
```

## Checking Status

```bash
# Get release status
helm status remedy

# Get release values
helm get values remedy

# List all releases
helm list
```

## Troubleshooting

### Check pod status

```bash
kubectl get pods -l app=remedy-app
```

### View application logs

```bash
kubectl logs -f deployment/remedy-app
```

### Check database connectivity

```bash
# PostgreSQL
kubectl exec -it statefulset/postgresql -- psql -U remedy -d remedy

# MongoDB
kubectl exec -it statefulset/mongodb -- mongosh remedy

# Redis
kubectl exec -it statefulset/redis -- redis-cli
```

### Debug Helm template rendering

```bash
# Render templates locally without installing
helm template remedy ./helm/remedy-back

# Render with custom values
helm template remedy ./helm/remedy-back -f custom-values.yaml
```

## Notes

- Make sure to update all `your-*` placeholder values in `values.yaml` before deploying to production
- For production environments, use Kubernetes Secrets management solutions like:
  - External Secrets Operator
  - Sealed Secrets
  - HashiCorp Vault
- Consider using Ingress for external access instead of LoadBalancer
- Set appropriate storage classes for your cloud provider
