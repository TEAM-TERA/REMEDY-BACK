# Helm 배포 가이드

REMEDY-BACK 프로젝트를 Helm으로 배포하는 방법입니다.

## 빠른 시작

### 1. Helm 설치 확인

```bash
helm version
```

Helm이 설치되어 있지 않다면:

```bash
# macOS
brew install helm

# Linux
curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
```

### 2. values.yaml 설정

`helm/remedy-back/values.yaml` 파일을 열어서 다음 값들을 실제 환경에 맞게 수정하세요:

### 3. Chart 검증

```bash
# Chart 문법 검사
helm lint ./helm/remedy-back

# 템플릿 렌더링 테스트
helm template remedy ./helm/remedy-back
```

### 4. 배포

```bash
# 기본 배포
helm install remedy ./helm/remedy-back

# 네임스페이스 지정
helm install remedy ./helm/remedy-back --namespace remedy --create-namespace

# Dry-run으로 미리 확인
helm install remedy ./helm/remedy-back --dry-run --debug
```

### 5. 배포 확인

```bash
# 배포 상태 확인
helm status remedy

# Pod 상태 확인
kubectl get pods

# 애플리케이션 로그 확인
kubectl logs -f deployment/remedy-app
```

## 주요 명령어

### 업그레이드

```bash
# values.yaml 수정 후
helm upgrade remedy ./helm/remedy-back

# 특정 값만 변경
helm upgrade remedy ./helm/remedy-back --set app.image.tag=v2.0.0
```

### 롤백

```bash
# 이전 버전으로 롤백
helm rollback remedy

# 특정 리비전으로 롤백
helm rollback remedy 1
```

### 삭제

```bash
# 완전 삭제 (PV는 유지됨)
helm uninstall remedy

# 네임스페이스까지 삭제
kubectl delete namespace remedy
```

### 히스토리 확인

```bash
# 배포 히스토리
helm history remedy

# 현재 설정 값 확인
helm get values remedy
```

## 환경별 배포

### 개발 환경

```bash
helm install remedy-dev ./helm/remedy-back \
  --set app.replicaCount=1 \
  --set postgresql.persistence.size=5Gi \
  --set mongodb.persistence.size=5Gi \
  --namespace dev --create-namespace
```

### 프로덕션 환경

별도의 `values-prod.yaml` 파일 생성:

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

mongodb:
  persistence:
    size: 50Gi

redis:
  persistence:
    size: 10Gi
```

배포:

```bash
helm install remedy-prod ./helm/remedy-back \
  -f values-prod.yaml \
  --namespace production --create-namespace
```

## 트러블슈팅

### Pod가 시작되지 않을 때

```bash
# Pod 상태 확인
kubectl get pods

# 상세 정보 확인
kubectl describe pod <pod-name>

# 로그 확인
kubectl logs <pod-name>
```

### 데이터베이스 연결 문제

```bash
# PostgreSQL 접속 테스트
kubectl exec -it statefulset/postgresql -- psql -U remedy -d remedy

# MongoDB 접속 테스트
kubectl exec -it statefulset/mongodb -- mongosh remedy

# Redis 접속 테스트
kubectl exec -it statefulset/redis -- redis-cli ping
```

### ConfigMap/Secret 확인

```bash
# ConfigMap 내용 확인
kubectl get configmap remedy-app-config -o yaml

# Secret 확인 (base64 디코딩)
kubectl get secret remedy-app-secret -o jsonpath='{.data.POSTGRES_PASSWORD}' | base64 -d
```

## 기존 YAML 파일과의 차이점

Helm을 사용하면 기존 개별 YAML 파일 대비 다음과 같은 장점이 있습니다:

1. **단일 명령으로 배포**: 여러 `kubectl apply -f` 명령 대신 `helm install` 한 번
2. **환경별 설정 관리**: values 파일로 dev/staging/prod 환경 분리
3. **버전 관리**: 자동 롤백, 히스토리 추적
4. **템플릿화**: 반복되는 값을 변수로 관리
5. **의존성 관리**: 애플리케이션과 데이터베이스를 하나의 차트로 관리

## 기존 배포에서 마이그레이션

기존에 개별 YAML 파일로 배포한 경우:

```bash
# 기존 리소스 삭제
kubectl delete -f spring-boot-deploy.yaml
kubectl delete -f spring-boot-service.yaml
kubectl delete -f spring-configmap.yaml
kubectl delete -f spring-secret.yaml
kubectl delete -f postgres-statefulset.yaml
kubectl delete -f postgres-service.yaml
kubectl delete -f mongo-statefulset.yaml
kubectl delete -f mongo-service.yaml
kubectl delete -f redis-statefulset.yaml
kubectl delete -f redis-service.yaml

# Helm으로 재배포
helm install remedy ./helm/remedy-back
```

**주의**: StatefulSet의 PVC는 자동으로 삭제되지 않으므로 데이터가 보존됩니다.

## 참고 자료

- [Helm 공식 문서](https://helm.sh/docs/)
- [Helm Chart 작성 가이드](https://helm.sh/docs/chart_template_guide/)
- [Values 파일 문서](./helm/remedy-back/README.md)
