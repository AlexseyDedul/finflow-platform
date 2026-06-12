#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

cd "$ROOT_DIR"

kubectl config use-context docker-desktop

kubectl apply -f infrastructure/k8s/namespace.yaml

kubectl apply -f infrastructure/k8s/postgres/
kubectl wait --for=condition=available deployment/postgres -n finflow --timeout=120s

kubectl apply -f infrastructure/k8s/keycloak/
kubectl wait --for=condition=available deployment/keycloak -n finflow --timeout=180s

kubectl apply -f infrastructure/k8s/minio/
kubectl wait --for=condition=available deployment/minio -n finflow --timeout=120s

kubectl apply -f infrastructure/k8s/minio/init-bucket-job.yaml || true
kubectl wait --for=condition=complete job/init-minio-bucket -n finflow --timeout=120s || true

kubectl apply -f infrastructure/k8s/localstack/
kubectl wait --for=condition=available deployment/localstack -n finflow --timeout=180s

kubectl apply -f infrastructure/k8s/localstack/init-sqs-job.yaml || true
kubectl wait --for=condition=complete job/init-localstack-sqs -n finflow --timeout=120s || true

docker build --platform linux/arm64 -t finflow-app:local ./finflow-app

kubectl apply -f infrastructure/k8s/finflow-app/
kubectl rollout restart deployment/finflow-app -n finflow
kubectl wait --for=condition=available deployment/finflow-app -n finflow --timeout=240s

kubectl get pods -n finflow
kubectl get svc -n finflow
