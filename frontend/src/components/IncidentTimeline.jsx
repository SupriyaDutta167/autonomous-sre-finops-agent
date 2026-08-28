import { formatTimestamp, formatSavings, formatStatus } from '../utils/formatters';
import { STATUS_COLORS, SEVERITY_COLORS } from '../utils/constants';

const IncidentTimeline = ({ incidents, loading }) => {
    return (
        <div className="panel h-full" style={{ maxHeight: '400px', overflowY: 'auto' }}>
            <div className="flex justify-between items-center mb-4 sticky top-0 bg-[#1e293b] pt-2 pb-2 border-b border-slate-700 z-10">
                <h2 className="panel-title" style={{ border: 'none', margin: 0 }}>Incident Timeline</h2>
                {loading && <span className="text-xs text-secondary animate-pulse">Syncing...</span>}
            </div>

            <div className="flex flex-col gap-4">
                {incidents.length === 0 && !loading && (
                    <div className="text-center py-8">
                        <div className="text-secondary mb-2">No incident history yet</div>
                        <div className="text-sm text-muted">Run a simulation to generate an incident.</div>
                    </div>
                )}
                {incidents.map((incident) => {
                    const statusColorClass = STATUS_COLORS[incident.status] || 'status-detected';
                    const sevColorClass = SEVERITY_COLORS[incident.action?.severity] || 'text-secondary';
                    const actionName = incident.action?.action || 'NO_ACTION';
                    const target = incident.targetInstance || incident.action?.target || 'N/A';
                    
                    return (
                        <div key={incident.id} className="p-3 border border-slate-700 rounded bg-slate-800/50 flex flex-col gap-2">
                            <div className="flex justify-between items-center">
                                <span className="mono text-xs text-secondary">{formatTimestamp(incident.timestamp)}</span>
                                <span className={`status-badge ${statusColorClass}`}>{formatStatus(incident.status)}</span>
                            </div>
                            
                            <div className="flex justify-between items-center">
                                <div>
                                    <span className="mono font-semibold text-blue-400">{actionName}</span>
                                    <span className="text-sm text-secondary ml-2">on {target}</span>
                                </div>
                                <span className={`text-xs ${sevColorClass}`}>{incident.action?.severity}</span>
                            </div>
                            
                            {incident.estimatedSavings > 0 && (
                                <div className="text-xs text-green-400 mt-1">
                                    Savings: {formatSavings(incident.estimatedSavings)}
                                </div>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default IncidentTimeline;
