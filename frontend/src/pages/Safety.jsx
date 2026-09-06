import { useData } from '../contexts/DataContext';
import { ShieldCheck } from 'lucide-react';
import { cn } from '../utils/cn';

export default function Safety() {
    const { latestIncident, simulatedResult } = useData();
    
    const rawData = simulatedResult || latestIncident;
    const decisionStatus = rawData?.decision?.status || rawData?.policyDecision?.status;
    const decisionReason = rawData?.decision?.reason || rawData?.policyDecision?.reason;
    const action = rawData?.action;

    const isBlocked = decisionStatus === 'BLOCKED';
    const isApproved = decisionStatus === 'APPROVED';
    const requiresApproval = decisionStatus === 'REQUIRES_APPROVAL' || action?.requiresApproval;

    return (
        <div className="flex flex-col gap-8 max-w-4xl mx-auto h-full">
            <div className="flex justify-between items-center">
                <h1 className="text-2xl font-medium">Safety & Policies</h1>
                <div className="px-3 py-1 rounded bg-success/10 text-success text-xs font-mono uppercase tracking-wider border border-success/20 flex items-center gap-2">
                    <ShieldCheck size={14} />
                    POLICY ENGINE ACTIVE
                </div>
            </div>
            
            {!rawData ? (
                <div className="p-12 rounded-xl bg-surface border border-border text-center text-secondary">
                    No active policy evaluations. Run a simulation to test the engine.
                </div>
            ) : (
                <div className="flex flex-col items-center max-w-2xl mx-auto w-full pt-8">
                    {/* Pipeline Visualization */}
                    <div className="w-full bg-subsurface border border-border p-4 rounded-lg flex justify-between items-center">
                        <div className="text-sm font-mono uppercase text-secondary">AI PROPOSAL</div>
                        <div className="text-sm font-mono text-accent">{action?.action}</div>
                    </div>
                    
                    <div className="h-8 w-px bg-border" />
                    
                    <div className="w-full bg-surface border-2 border-border p-5 rounded-xl shadow-lg relative flex flex-col items-center">
                        <div className="absolute -top-3 bg-page px-2 text-xs font-mono text-secondary tracking-wider">EVALUATION</div>
                        <ShieldCheck size={32} className="text-secondary mb-3 mt-2" />
                        <div className="text-center text-sm mb-2">{decisionReason || "Evaluating against organizational safety rules..."}</div>
                    </div>
                    
                    <div className="h-8 w-px bg-border" />
                    
                    <div className={cn(
                        "w-full p-4 rounded-lg flex flex-col items-center border",
                        isApproved ? "bg-success/5 border-success/30 text-success" : 
                        isBlocked ? "bg-danger/5 border-danger/30 text-danger" : 
                        "bg-warning/5 border-warning/30 text-warning"
                    )}>
                        <div className="text-sm font-mono uppercase tracking-wider mb-2">RESULT: {decisionStatus || 'PENDING'}</div>
                        <div className="font-medium text-lg flex items-center gap-2">
                            {isApproved && "✓ EXECUTION ALLOWED"}
                            {isBlocked && "✕ EXECUTION PREVENTED"}
                            {requiresApproval && "⚠ HUMAN APPROVAL REQUIRED"}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
