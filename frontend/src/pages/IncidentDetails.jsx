import { useParams, useNavigate } from 'react-router-dom';
import { useData } from '../contexts/DataContext';
import { ArrowLeft, ShieldAlert } from 'lucide-react';
import { formatTimestamp, formatSavings } from '../utils/formatters';

const Step = ({ label, children, active = true, isError = false }) => (
    <div className={`flex flex-col gap-2 p-4 rounded-lg border ${active ? (isError ? 'bg-danger/5 border-danger/30' : 'bg-surface border-border') : 'bg-page border-border/50 opacity-50 grayscale'}`}>
        <div className="text-xs font-mono uppercase tracking-wider text-secondary">{label}</div>
        <div className="text-sm font-medium">{children}</div>
    </div>
);

export default function IncidentDetails() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { incidents } = useData();

    // Since we don't have a stable ID, we match by index or timestamp.
    // For this redesign, assuming the route passes the index as the ID string.
    const incidentIndex = parseInt(id, 10);
    const incident = incidents[incidentIndex];

    if (!incident) {
        return (
            <div className="flex flex-col items-center justify-center h-full gap-4">
                <div className="text-secondary">Incident not found</div>
                <button onClick={() => navigate('/incidents')} className="text-accent hover:underline">
                    Return to Incidents
                </button>
            </div>
        );
    }

    const action = incident.action?.action || "UNKNOWN";
    const target = incident.action?.target || "UNKNOWN";
    const decisionStatus = incident.decision?.status || incident.policyDecision?.status;
    const isBlocked = decisionStatus === 'BLOCKED';
    const execution = incident.executionResult;
    const verification = incident.verificationResult;
    const finOps = incident.finOpsResult;
    const savings = finOps?.estimatedMonthlySavings ?? incident.estimatedSavings ?? 0;

    return (
        <div className="flex flex-col gap-6 max-w-5xl mx-auto h-full pb-10">
            <button onClick={() => navigate('/incidents')} className="flex items-center gap-2 text-secondary hover:text-primary transition-colors self-start mb-2">
                <ArrowLeft size={16} />
                <span className="text-sm font-medium">Back to Incidents</span>
            </button>

            <div className="flex justify-between items-end border-b border-border pb-6">
                <div>
                    <div className="text-xs font-mono text-secondary mb-2">{formatTimestamp(incident.timestamp)}</div>
                    <h1 className="text-3xl font-medium tracking-tight">Incident on {target}</h1>
                </div>
                <div className={`px-3 py-1 rounded text-xs font-bold uppercase tracking-wider ${isBlocked ? 'bg-danger/10 text-danger border border-danger/20' : 'bg-success/10 text-success border border-success/20'}`}>
                    {incident.status || incident.finalStatus || 'UNKNOWN'}
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mt-4">
                <Step label="1. Detect">
                    <span className="font-mono text-accent">{incident.alert?.type || "SYSTEM ALERT"}</span>
                </Step>
                <Step label="2. AI Analysis">
                    <span className="font-mono text-primary">{action}</span>
                </Step>
                <Step label="3. Policy Decision" isError={isBlocked}>
                    {decisionStatus || 'PENDING'}
                </Step>
                <Step label="4. Execution" active={!isBlocked}>
                    {isBlocked ? 'NOT EXECUTED' : execution?.executed ? 'EXECUTED' : 'PENDING'}
                </Step>
                <Step label="5. Verification" active={!isBlocked}>
                    {isBlocked ? 'N/A' : verification?.verified ? 'VERIFIED' : 'PENDING'}
                </Step>
                <Step label="6. FinOps Impact" active={!isBlocked}>
                    {isBlocked ? '$0.00' : <span className="text-success">+{formatSavings(savings)}</span>}
                </Step>
            </div>

            {isBlocked && (
                <div className="mt-6 p-4 rounded-lg bg-danger/10 border border-danger/20 flex items-start gap-3">
                    <ShieldAlert className="text-danger shrink-0 mt-0.5" size={20} />
                    <div>
                        <div className="text-danger font-medium mb-1">✕ EXECUTION PREVENTED</div>
                        <div className="text-danger/80 text-sm">Policy Engine blocked this action. Reason: {incident.decision?.reason || incident.policyDecision?.reason}</div>
                    </div>
                </div>
            )}
        </div>
    );
}
