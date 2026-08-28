import { STATUS_COLORS } from '../utils/constants';
import { formatStatus } from '../utils/formatters';

const PolicyGuardrailPanel = ({ decisionStatus, decisionReason, actionTarget, actionType }) => {
    if (!decisionStatus) {
        return (
            <div className="panel flex flex-col justify-center items-center h-full text-center">
                <div className="text-secondary mb-2">No pending actions</div>
                <div className="text-sm text-muted">Awaiting AI proposals for policy evaluation.</div>
            </div>
        );
    }

    let guardrailClass = '';
    if (decisionStatus === 'APPROVED') guardrailClass = 'guardrail-approved';
    else if (decisionStatus === 'BLOCKED') guardrailClass = 'guardrail-blocked';
    else if (decisionStatus === 'REQUIRES_APPROVAL') guardrailClass = 'guardrail-approval';

    const statusBadgeClass = STATUS_COLORS[decisionStatus] || 'status-detected';

    return (
        <div className={`panel ${guardrailClass} h-full`}>
            <div className="flex justify-between items-center mb-4">
                <h2 className="panel-title" style={{ borderBottom: 'none', margin: 0 }}>Policy Guardrail</h2>
                <span className={`status-badge ${statusBadgeClass}`}>
                    {formatStatus(decisionStatus)}
                </span>
            </div>

            {actionType && actionTarget && (
                <div className="mono text-sm mb-4 opacity-75">
                    {actionType} → {actionTarget}
                </div>
            )}

            <div className="mt-2 text-lg">
                {decisionReason}
            </div>

            {decisionStatus === 'BLOCKED' && (
                <div className="mt-4 text-red-500 font-semibold text-sm">
                    ✕ EXECUTION PREVENTED
                </div>
            )}
        </div>
    );
};

export default PolicyGuardrailPanel;
