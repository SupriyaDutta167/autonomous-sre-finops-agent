import { formatConfidence } from '../utils/formatters';
import { SEVERITY_COLORS } from '../utils/constants';

const AgentDecisionCard = ({ action }) => {
    if (!action) {
        return (
            <div className="panel flex flex-col justify-center items-center h-full text-center">
                <div className="text-secondary mb-2">No active incident</div>
                <div className="text-sm text-muted">Simulation controls are ready.</div>
            </div>
        );
    }

    const severityColor = SEVERITY_COLORS[action.severity] || 'text-secondary';

    return (
        <div className="panel">
            <h2 className="panel-title">AI Decision</h2>
            
            <div className="flex justify-between items-center mb-4">
                <div className="text-2xl mono text-blue-400">{action.action}</div>
                <div className="mono bg-slate-800 px-2 py-1 rounded border border-slate-700">
                    {action.target}
                </div>
            </div>

            <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                    <div className="metric-label">Confidence</div>
                    <div className="text-xl">{formatConfidence(action.confidence)}</div>
                </div>
                <div>
                    <div className="metric-label">Severity</div>
                    <div className={`text-xl ${severityColor}`}>{action.severity}</div>
                </div>
            </div>

            <div className="mb-4">
                <div className="metric-label">Root Cause</div>
                <div className="mt-1">{action.rootCause}</div>
            </div>
            
            <div className="mb-4">
                <div className="metric-label">Reasoning</div>
                <div className="mt-1">{action.reason}</div>
            </div>

            {action.requiresApproval && (
                <div className="mt-4 p-2 bg-yellow-500/10 border border-yellow-500 rounded text-yellow-500 text-sm flex items-center justify-center">
                    Requires Human Approval
                </div>
            )}
        </div>
    );
};

export default AgentDecisionCard;
