finops-agent/
│
├── README.md
├── .gitignore
│
├── backend/
│   ├── pom.xml
│   │
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── sreagent/
│       │   │           └── finops/
│       │   │
│       │   │               ├── FinopsAgentApplication.java
│       │   │               │
│       │   │               ├── config/
│       │   │               │   ├── GeminiConfig.java
│       │   │               │   └── GoogleCloudConfig.java
│       │   │               │
│       │   │               ├── controller/
│       │   │               │   ├── AlertWebhookController.java
│       │   │               │   ├── DashboardController.java
│       │   │               │   └── SimulationController.java
│       │   │               │
│       │   │               ├── model/
│       │   │               │   ├── SystemAlert.java
│       │   │               │   ├── SreAction.java
│       │   │               │   ├── IncidentAuditLog.java
│       │   │               │   ├── ActionType.java
│       │   │               │   ├── IncidentStatus.java
│       │   │               │   ├── Severity.java
│       │   │               │   └── PolicyDecision.java
│       │   │               │
│       │   │               ├── service/
│       │   │               │   ├── IncidentOrchestrator.java
│       │   │               │   ├── GeminiSreService.java
│       │   │               │   ├── GcpComputeService.java
│       │   │               │   ├── FinOpsService.java
│       │   │               │   ├── TelemetryService.java
│       │   │               │   ├── VerificationService.java
│       │   │               │   └── AuditLogService.java
│       │   │               │
│       │   │               ├── safety/
│       │   │               │   ├── PolicyEngine.java
│       │   │               │   ├── ActionValidator.java
│       │   │               │   └── SafetyPolicy.java
│       │   │               │
│       │   │               ├── simulation/
│       │   │               │   ├── TelemetrySimulator.java
│       │   │               │   └── IncidentScenario.java
│       │   │               │
│       │   │               └── exception/
│       │   │                   ├── GlobalExceptionHandler.java
│       │   │                   ├── AiServiceException.java
│       │   │                   ├── GcpExecutionException.java
│       │   │                   └── UnsafeActionException.java
│       │   │
│       │   └── resources/
│       │       ├── application.properties
│       │       └── prompts/
│       │           ├── sre-system-prompt.txt
│       │           └── finops-system-prompt.txt
│       │
│       └── test/
│           └── java/
│               └── com/
│                   └── sreagent/
│                       └── finops/
│                           ├── GeminiSreServiceTest.java
│                           ├── PolicyEngineTest.java
│                           ├── FinOpsServiceTest.java
│                           └── IncidentOrchestratorTest.java
│
│
└── frontend/
├── package.json
├── vite.config.js
├── index.html
│
├── public/
│   ├── architecture.png
│   └── logo.svg
│
└── src/
├── main.jsx
├── App.jsx
├── index.css
│
├── components/
│   ├── Header.jsx
│   ├── SystemHealthOverview.jsx
│   ├── ClusterMetrics.jsx
│   ├── AgentDecisionCard.jsx
│   ├── AgentLogTerminal.jsx
│   ├── PolicyGuardrailPanel.jsx
│   ├── IncidentTimeline.jsx
│   ├── BeforeAfterMetrics.jsx
│   ├── FinOpsSavingsCard.jsx
│   └── SimulationControls.jsx
│
├── pages/
│   └── Dashboard.jsx
│
├── services/
│   └── api.js
│
├── hooks/
│   ├── useTelemetry.js
│   └── useIncidentStream.js
│
├── utils/
│   ├── formatters.js
│   └── constants.js
│
└── assets/
└── icons/