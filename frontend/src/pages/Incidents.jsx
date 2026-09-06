import { useData } from '../contexts/DataContext';
import { formatTimestamp, formatSavings } from '../utils/formatters';
import { useNavigate } from 'react-router-dom';

export default function Incidents() {
    const { incidents } = useData();
    const navigate = useNavigate();

    return (
        <div className="flex flex-col gap-6 max-w-6xl mx-auto h-full">
            <div className="flex justify-between items-center">
                <h1 className="text-2xl font-medium">Incident History</h1>
            </div>

            <div className="bg-surface border border-border rounded-xl overflow-hidden flex-1 flex flex-col">
                <div className="overflow-x-auto custom-scrollbar flex-1">
                    <table className="w-full text-left border-collapse whitespace-nowrap">
                        <thead className="bg-subsurface sticky top-0 z-10">
                            <tr>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider border-b border-border">Time</th>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider border-b border-border">Action</th>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider border-b border-border">Target</th>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider border-b border-border">Severity</th>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider border-b border-border">Status</th>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider border-b border-border">Savings</th>
                            </tr>
                        </thead>
                        <tbody className="text-sm">
                            {incidents.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="text-center py-12 text-secondary">
                                        No incident history available. Run a simulation to generate an incident.
                                    </td>
                                </tr>
                            ) : (
                                incidents.map((inc, i) => {
                                    const action = inc.action?.action || "UNKNOWN";
                                    const target = inc.action?.target || "UNKNOWN";
                                    const severity = inc.action?.severity || "UNKNOWN";
                                    const status = inc.status || inc.finalStatus || "UNKNOWN";
                                    const savings = inc.finOpsResult?.estimatedMonthlySavings ?? inc.estimatedSavings ?? 0;

                                    return (
                                        <tr 
                                            key={i} 
                                            className="border-b border-border/50 hover:bg-subsurface/50 transition-colors cursor-pointer group"
                                            onClick={() => navigate(`/incidents/${i}`)}
                                        >
                                            <td className="p-4 font-mono text-secondary group-hover:text-primary transition-colors">{formatTimestamp(inc.timestamp)}</td>
                                            <td className="p-4 font-mono text-accent">{action}</td>
                                            <td className="p-4 font-mono">{target}</td>
                                            <td className="p-4">
                                                <span className={`text-xs uppercase font-medium ${severity === 'CRITICAL' ? 'text-danger' : severity === 'HIGH' ? 'text-warning' : 'text-success'}`}>
                                                    {severity}
                                                </span>
                                            </td>
                                            <td className="p-4">
                                                <div className="flex items-center gap-2">
                                                    <div className={`w-1.5 h-1.5 rounded-full ${status === 'RESOLVED' ? 'bg-success' : status === 'BLOCKED' ? 'bg-danger' : 'bg-warning'}`} />
                                                    <span className="text-xs uppercase">{status}</span>
                                                </div>
                                            </td>
                                            <td className="p-4 font-mono text-success">
                                                {savings > 0 ? `+${formatSavings(savings)}` : '$0.00'}
                                            </td>
                                        </tr>
                                    );
                                })
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}
