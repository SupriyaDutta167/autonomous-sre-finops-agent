import { useState } from 'react';
import Header from '../components/Header';
import SystemHealthOverview from '../components/SystemHealthOverview';
import SimulationControls from '../components/SimulationControls';
import FinOpsSavingsCard from '../components/FinOpsSavingsCard';
import ClusterMetrics from '../components/ClusterMetrics';
import AgentDecisionCard from '../components/AgentDecisionCard';
import PolicyGuardrailPanel from '../components/PolicyGuardrailPanel';
import BeforeAfterMetrics from '../components/BeforeAfterMetrics';
import IncidentTimeline from '../components/IncidentTimeline';
import AgentLogTerminal from '../components/AgentLogTerminal';
import { useTelemetry } from '../hooks/useTelemetry';
import { useIncidentStream } from '../hooks/useIncidentStream';

const Dashboard = () => {
    const [backendHealth, setBackendHealth] = useState(null);
    const { vms, loading: vmsLoading, refreshVms } = useTelemetry();
    const { incidents, latestIncident, loading: incidentsLoading, refreshIncidents } = useIncidentStream();
    
    // We maintain a local latest result when a simulation completes to immediately show it
    const [simulatedResult, setSimulatedResult] = useState(null);

    const handleSimulationComplete = (result) => {
        // Result is OrchestrationResult
        setSimulatedResult(result);
        refreshVms();
        refreshIncidents();
    };

    // Determine what to show in the detailed panels. 
    // Prefer the immediately returned simulation result for smooth UX, otherwise fall back to latest incident
    const rawData = simulatedResult || latestIncident;
    const normalizedData = rawData ? {
        alert: rawData.alert,
        action: rawData.action,
        decisionStatus: rawData.decision?.status || rawData.policyDecision?.status,
        decisionReason: rawData.decision?.reason || rawData.policyDecision?.reason,
        finalStatus: rawData.finalStatus || rawData.status,
        executionResult: rawData.executionResult,
        verificationResult: rawData.verificationResult,
        finOpsResult: rawData.finOpsResult,
        savings: rawData.finOpsResult?.estimatedMonthlySavings ?? rawData.estimatedSavings ?? 0,
        timestamp: rawData.timestamp || new Date().toISOString()
    } : null;

    return (
        <div className="dashboard-layout">
            <Header backendHealth={backendHealth} />

            {/* Top Row: System Health / Summary Cards */}
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-6">
                <SystemHealthOverview onHealthChange={setBackendHealth} />
                <div className="panel flex flex-col justify-center">
                    <div className="metric-label mb-2">Recorded Incidents</div>
                    <div className="text-2xl font-semibold">{incidents.length}</div>
                    <div className="text-xs text-secondary mt-1">Recorded events</div>
                </div>
                <FinOpsSavingsCard 
                    savings={normalizedData?.savings} 
                    decisionStatus={normalizedData?.decisionStatus} 
                    isPending={normalizedData && !normalizedData?.finalStatus}
                />
                <div className="panel flex flex-col justify-center">
                    <div className="metric-label mb-2">Infrastructure</div>
                    <div className="text-2xl font-semibold">{vms.length}</div>
                    <div className="text-xs text-secondary mt-1">Simulated VMs</div>
                </div>
            </div>

            {/* Simulation Controls */}
            <SimulationControls onSimulationComplete={handleSimulationComplete} />

            {/* Infrastructure & AI Decision */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <ClusterMetrics vms={vms} loading={vmsLoading} />
                <AgentDecisionCard action={normalizedData?.action} />
            </div>

            {/* Policy & Execution */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <PolicyGuardrailPanel 
                    decisionStatus={normalizedData?.decisionStatus}
                    decisionReason={normalizedData?.decisionReason}
                    actionType={normalizedData?.action?.action}
                    actionTarget={normalizedData?.action?.target}
                />
                <BeforeAfterMetrics 
                    executionResult={normalizedData?.executionResult}
                    verificationResult={normalizedData?.verificationResult}
                    decisionStatus={normalizedData?.decisionStatus}
                />
            </div>

            {/* Logs & Timeline */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 pb-8">
                <IncidentTimeline incidents={incidents} loading={incidentsLoading} />
                <AgentLogTerminal normalizedData={normalizedData} />
            </div>
        </div>
    );
};

export default Dashboard;
