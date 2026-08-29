# Autonomous SRE + FinOps Agent

## 1. What the project is
This is an autonomous SRE (Site Reliability Engineering) and FinOps agent built for a hackathon. It monitors simulated infrastructure telemetry, proposes automated remediation actions (like scaling up or restarting) or cost optimization actions (like stopping idle VMs), enforces strict safety policies before execution, and estimates potential savings.

## 2. Architecture
The architecture comprises a Java Spring Boot backend and a React/Vite frontend. The backend flow ensures strict separation of concerns:
- **Controller**: HTTP concerns.
- **IncidentOrchestrator**: Workflow coordination.
- **SreReasoningEngine**: Abstract AI reasoning.
- **ActionValidator & PolicyEngine**: Structural validation and deterministic safety authorization.
- **InfrastructureExecutor & SimulationExecutor**: Execution abstraction (currently running in simulation mode).
- **VerificationService**: Post-action verification.
- **FinOpsService**: Estimating savings.
- **AuditLogService**: Recording the incident lifecycle.

## 3. SRE Workflow
1. System alerts (telemetry) are received.
2. AI analyzes the telemetry and proposes an `SreAction`.
3. The Action Validator and Policy Engine verify the action.
4. If `APPROVED`, the Infrastructure Executor executes the action.
5. The Verification Service verifies the new state.
6. The FinOps Service calculates estimated savings.
7. The Audit Log Service records the resolution.

## 4. Safety Model
The safety model is the core invariant of the system:
**AI proposes. Policy authorizes. Executor executes ONLY after approval.**
Any unsafe action (e.g., stopping a production database) is strictly `BLOCKED` by the Policy Engine. Blocked actions prevent execution, verification, and savings realization.

## 5. FinOps Model
FinOps estimates savings based on successful reliability actions. 
- Positive savings (e.g., $150.0/month) are estimated for stopping idle resources.
- Reliability actions like `SCALE_UP` or `RESTART_VM` yield $0 direct savings.
- `BLOCKED`, `REQUIRES_APPROVAL`, and `FAILED` actions always record $0 savings.
All reported values are **simulated estimated monthly savings**, not actual billing data.

## 6. Simulation and GCP Mode
By default, the project runs in **SIMULATION MODE**. It uses a `SimulationExecutor` and `TelemetrySimulator` to mutate an in-memory infrastructure state. 
- No GCP credentials required.
- No billing required.

### Optional GCP Mode
The project provides an optional `GcpComputeService` adapter for real Google Cloud Compute Engine integration.
- To enable, activate the `gcp` Spring profile and configure `finops.gcp.project-id` and `finops.gcp.compute-zone`.
- Requires appropriate standard Google Cloud authentication (e.g. ADC).

### Mutation Safety
**Real GCP mutations are disabled by default.**
To allow actual infrastructure changes, you must explicitly set `finops.gcp.mutations.enabled=true`. If false (the default), the GCP executor will safely refuse mutation commands.

### Architecture
The execution boundary delegates to either simulation or GCP:
`InfrastructureExecutor` -> `SimulationExecutor` (default) or `GcpComputeService`
`InfrastructureStateProvider` -> `SimulationExecutor` (default) or `GcpInfrastructureStateProvider`

### Important Disclaimer
**Real GCP infrastructure mutations were not performed during development/testing.** The GCP integration demonstrates structural pluggability. Real billing integration is out of scope.

## 7. Gemini Integration
The system integrates with Google's Gemini AI via Spring AI to process telemetry and propose remediation actions. The `GeminiSreService` provides the reasoning capability, while a deterministic `MockSreReasoningService` is available for offline or testing purposes. The frontend never communicates directly with Gemini.

## 8. Backend Setup
Prerequisites: Java 21+, Maven.
1. Navigate to `backend/`.
2. Configure `GEMINI_API_KEY` in your environment variables.
3. Run `mvnw clean compile` to compile.
4. Run `mvnw spring-boot:run` to start the backend on port 8080.

## 9. Frontend Setup
Prerequisites: Node.js 18+, npm.
1. Navigate to `frontend/`.
2. Run `npm install` to install dependencies.
3. Run `npm run dev` to start the dashboard on port 5173.

## 10. Available REST Endpoints
- `GET /api/health`
- `GET /api/incidents`
- `GET /api/simulation/vms`
- `POST /api/simulate/cpu-spike`
- `POST /api/simulate/memory-leak`
- `POST /api/simulate/traffic-surge`
- `POST /api/simulate/idle-vm`
- `POST /api/simulate/unsafe-action`
- `POST /api/alerts`

## 11. Demo Scenarios
- **CPU Spike**: AI recommends `SCALE_UP` -> Approved -> Executed -> Verified.
- **Idle VM**: AI identifies cost savings -> `STOP_VM` -> Approved -> Executed -> Verified -> $150.0/mo estimated savings.
- **Traffic Surge**: AI handles traffic spike for `prod-web-03` -> `SCALE_UP` -> Approved -> Executed -> Verified.
- **Memory Leak**: AI resolves memory leak -> `RESTART_VM` -> Approved -> Executed -> Verified.
- **Unsafe Action**: AI proposes `STOP_VM` on `prod-db-01` -> Policy `BLOCKED` -> Execution Prevented -> $0 savings.

## 12. Limitations
- Entirely simulated: no real cloud integration.
- In-memory audit logging: state is lost on restart.
- No authentication or authorization mechanisms.
- Mock telemetry data is simplified.
