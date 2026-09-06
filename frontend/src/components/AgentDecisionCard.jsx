import { formatConfidence } from '../utils/formatters';

const SEVERITY_COLORS = {
    CRITICAL: 'text-danger',
    HIGH: 'text-warning',
    MEDIUM: 'text-accent',
    LOW: 'text-success'
};

export default function AgentDecisionCard({ action }) {
    if (!action) {
        return (
            <div className="p-6 rounded-xl bg-surface border border-border flex flex-col justify-center items-center h-full text-center">
                <div className="text-secondary mb-2">No Active AI Decision</div>
                <div className="text-sm text-muted">Awaiting agent analysis or simulation trigger.</div>
            </div>
        );
    }

    const severityColor = SEVERITY_COLORS[action.severity] || 'text-secondary';

    return (
        <div className="p-6 rounded-xl bg-surface border border-border h-full flex flex-col gap-5">
            <div className="flex justify-between items-start gap-4">
                <div className="flex flex-col gap-1">
                    <div className="text-xs font-mono text-secondary uppercase tracking-wider">Action Proposal</div>
                    <div className="text-2xl font-mono text-accent">{action.action}</div>
                </div>
                <div className="text-sm font-mono bg-subsurface px-3 py-1.5 rounded-lg border border-border text-primary whitespace-nowrap">
                    {action.target}
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4 p-4 bg-subsurface rounded-lg border border-border">
                <div className="flex flex-col gap-1">
                    <div className="text-xs font-mono text-secondary uppercase tracking-wider">Confidence</div>
                    <div className="text-lg font-medium">{formatConfidence(action.confidence)}</div>
                </div>
                <div className="flex flex-col gap-1">
                    <div className="text-xs font-mono text-secondary uppercase tracking-wider">Severity</div>
                    <div className={`text-lg font-medium ${severityColor}`}>{action.severity}</div>
                </div>
            </div>

            <div className="flex flex-col gap-2">
                <div className="text-xs font-mono text-secondary uppercase tracking-wider">Root Cause</div>
                <div className="text-sm leading-relaxed">{action.rootCause}</div>
            </div>
            
            <div className="flex flex-col gap-2">
                <div className="text-xs font-mono text-secondary uppercase tracking-wider">Reasoning</div>
                <div className="text-sm leading-relaxed text-secondary">{action.reason}</div>
            </div>

            {action.requiresApproval && (
                <div className="mt-auto pt-4 border-t border-border">
                    <div className="px-3 py-2 bg-warning/10 border border-warning/20 rounded-lg text-warning text-xs font-medium uppercase tracking-wide flex items-center justify-center">
                        Requires Human Approval
                    </div>
                </div>
            )}
        </div>
    );
}
