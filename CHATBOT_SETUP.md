# AI Chatbot Setup Guide

## Phase 1: Ollama Infrastructure (Completed)

### Deployed Resources

1. **Persistent Volume Claim** (`ollama-pvc.yaml`)
   - 20Gi storage for model data
   - Storage class: local-path (K3s default)

2. **Ollama Deployment** (`ollama-deployment.yaml`)
   - Image: ollama/ollama:latest
   - Resources: 4Gi RAM request, 6Gi limit, 1-2 CPU
   - Uses quantized PLLuM model (Q4_K_M) for efficiency

3. **Ollama Service** (`ollama-service.yaml`)
   - ClusterIP service on port 11434
   - Internal access only (not exposed externally)
   - URL: http://ollama-service:11434

4. **Model Download Job** (`ollama-init-job.yaml`)
   - Downloads PLLuM Q4_K_M model on first deployment
   - Runs automatically via GitHub Actions

### Configuration

Added to `configmap.yaml`:
- `OLLAMA_BASE_URL`: http://ollama-service:11434
- `CHATBOT_MARGIN_PERCENT`: 30 (default margin for parts pricing)
- `CHATBOT_LABOR_RATE_PLN`: 150 (default labor rate)

### Deployment

The GitHub Actions workflow automatically deploys Ollama infrastructure on every push to main.

Manual deployment:
```bash
kubectl apply -f kubernetes/ollama-pvc.yaml
kubectl apply -f kubernetes/ollama-deployment.yaml
kubectl apply -f kubernetes/ollama-service.yaml
kubectl apply -f kubernetes/ollama-init-job.yaml
```

### Verify Ollama

Check if Ollama is running:
```bash
kubectl get pods -n bavarians-prod -l app=ollama
kubectl logs -n bavarians-prod -l app=ollama
```

Test Ollama API:
```bash
kubectl exec -it deployment/ollama -n bavarians-prod -- curl http://localhost:11434/api/tags
```

Check model download job:
```bash
kubectl get jobs -n bavarians-prod
kubectl logs -n bavarians-prod job/ollama-model-download
```

## Next Steps

### Phase 2: Backend Foundation
- [ ] Create OllamaService for HTTP client communication
- [ ] Create ChatbotService for session management
- [ ] Create ChatbotController with REST API endpoints
- [ ] Create entity models and DTOs

### Phase 3: Inter Cars Integration
- [ ] Obtain Inter Cars API credentials
- [ ] Create InterCarsApiClient
- [ ] Implement parts search and pricing with margin
- [ ] Add price caching (24h TTL)

### Phase 4-9: See full plan in `/Users/magdalena.dabrowska/.claude/plans/cheeky-floating-emerson.md`

## Resource Monitoring

Monitor Ollama resource usage:
```bash
kubectl top pod -n bavarians-prod -l app=ollama
```

If VPS runs out of resources, consider:
1. Using a smaller quantized model (Q3 or Q2)
2. Reducing Ollama memory limits
3. Running Ollama on external server
