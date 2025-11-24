# Helm 배포 가이드

## 초기 설정

### 1. Values 파일 생성

실제 배포를 위해 `values.yaml` 파일을 생성합니다:

```bash
cd helm/remedy-back
cp values.example.yaml values.yaml
```

### 2. 민감한 정보 설정

`values.yaml` 파일을 열어 다음 값들을 실제 값으로 변경하세요:

#### PostgreSQL
```yaml
postgresql:
  password: "YOUR_POSTGRES_PASSWORD"
```

#### AWS S3
```yaml
aws:
  s3:
    bucket: "your-s3-bucket-name"
  accessKey: "YOUR_AWS_ACCESS_KEY"
  secretKey: "YOUR_AWS_SECRET_KEY"
```

#### JWT Secret
```yaml
jwt:
  secretKey: "YOUR_JWT_SECRET_KEY_AT_LEAST_32_CHARACTERS_LONG"
```

#### Elasticsearch
```yaml
elasticsearch:
  host: "YOUR_ELASTICSEARCH_HOST"
  port: 9200
```

### 3. JWT Secret Key 생성

안전한 JWT Secret Key를 생성하려면:

```bash
# 방법 1: OpenSSL 사용
openssl rand -base64 64

# 방법 2: Python 사용
python3 -c "import secrets; print(secrets.token_urlsafe(64))"
```

## 배포 방법

### 로컬 Kubernetes (kind)

#### 1. Docker 이미지 빌드
```bash
./gradlew clean bootJar
docker build -t remedy-back:latest .
```

#### 2. Kind 클러스터에 이미지 로드
```bash
kind load docker-image remedy-back:latest --name remedy
```

#### 3. Helm 설치
```bash
helm install remedy ./helm/remedy-back --namespace default
```

#### 4. Helm 업그레이드
```bash
helm upgrade remedy ./helm/remedy-back --namespace default
```

### 프로덕션 환경

프로덕션 환경별로 별도의 values 파일을 사용하는 것을 권장합니다:

```bash
# 개발 환경
helm install remedy ./helm/remedy-back -f values-dev.yaml

# 스테이징 환경
helm install remedy ./helm/remedy-back -f values-staging.yaml

# 프로덕션 환경
helm install remedy ./helm/remedy-back -f values-prod.yaml
```

## 보안 관리

### Git에 올리지 말아야 할 파일
- `values.yaml` - 실제 비밀번호와 키가 포함된 파일
- `values-*.yaml` - 환경별 설정 파일

### Git에 올려야 할 파일
- `values.example.yaml` - 템플릿 파일
- 모든 template 파일들

### 권장 사항

1. **Kubernetes Secrets 사용**: 민감한 정보는 별도의 Kubernetes Secret으로 관리
2. **Sealed Secrets**: GitOps를 사용한다면 Sealed Secrets 사용 고려
3. **External Secrets Operator**: AWS Secrets Manager, HashiCorp Vault 등과 연동
4. **SOPS**: 암호화된 값을 Git에 저장

## 트러블슈팅

### MongoDB가 Ready 상태가 안 될 때
```bash
kubectl logs mongodb-0 -n default
kubectl describe pod mongodb-0 -n default
```

### 애플리케이션 로그 확인
```bash
kubectl logs -f deployment/remedy-app -n default
```

### 전체 재배포
```bash
helm uninstall remedy --namespace default
helm install remedy ./helm/remedy-back --namespace default
```
