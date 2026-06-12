#!/usr/bin/env bash
set -euo pipefail

kubectl config use-context docker-desktop
kubectl delete namespace finflow --ignore-not-found=true