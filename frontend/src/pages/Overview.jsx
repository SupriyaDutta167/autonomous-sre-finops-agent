import { useData } from '../contexts/DataContext';
import SystemHealthOverview from '../components/SystemHealthOverview';
import SimulationControls from '../components/SimulationControls';
import ClusterMetrics from '../components/ClusterMetrics';
import AgentDecisionCard from '../components/AgentDecisionCard';

export default function Overview() {
    const { incidents, vms, loadingVms, simulatedResult, latestIncident } = useData();

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

    // Calculate total estimated savings
    const totalSavings = incidents.reduce((acc, inc) => {
        const amt = inc.finOpsResult?.estimatedMonthlySavings ?? inc.estimatedSavings ?? 0;
        return acc + amt;
    }, 0);

    return (
        <div className="flex flex-col gap-6 max-w-[1440px] mx-auto">
            {/* Top Summary */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                <SystemHealthOverview />
                
                <div className="p-5 rounded-xl bg-surface border border-border flex flex-col justify-center">
                    <div className="text-xs font-mono text-secondary uppercase tracking-wider mb-2">Recorded Incidents</div>
                    <div className="text-3xl font-semibold">{incidents.length}</div>
                    <div className="text-sm text-secondary mt-1">Total recorded events</div>
                </div>
                
                <div className="p-5 rounded-xl bg-surface border border-border flex flex-col justify-center">
                    <div className="text-xs font-mono text-secondary uppercase tracking-wider mb-2">Estimated Savings</div>
                    <div className="text-3xl font-semibold text-success">${totalSavings.toFixed(2)}</div>
                    <div className="text-sm text-secondary mt-1">Monthly simulated estimate</div>
                </div>
                
                <div className="p-5 rounded-xl bg-surface border border-border flex flex-col justify-center">
                    <div className="text-xs font-mono text-secondary uppercase tracking-wider mb-2">System Health</div>
                    <div className="text-3xl font-semibold">{vms.length}</div>
                    <div className="text-sm text-secondary mt-1">Simulated VMs active</div>
                </div>
            </div>

            {/* Simulation Controls */}
            <SimulationControls />

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="flex flex-col gap-4">
                    <h2 className="text-lg font-medium">Current Incident</h2>
                    <AgentDecisionCard action={normalizedData?.action} />
                </div>
                <div className="flex flex-col gap-4">
                    <h2 className="text-lg font-medium">Infrastructure Health</h2>
                    <ClusterMetrics vms={vms} loading={loadingVms} />
                </div>
            </div>
        </div>
    );
}
