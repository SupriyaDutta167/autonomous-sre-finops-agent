import { useData } from '../contexts/DataContext';
import { formatSavings, formatTimestamp } from '../utils/formatters';
import { TrendingDown, LineChart } from 'lucide-react';

export default function FinOps() {
    const { incidents } = useData();

    const totalSavings = incidents.reduce((acc, inc) => {
        const amt = inc.finOpsResult?.estimatedMonthlySavings ?? inc.estimatedSavings ?? 0;
        return acc + amt;
    }, 0);

    const savingsEvents = incidents.filter(inc => {
        const amt = inc.finOpsResult?.estimatedMonthlySavings ?? inc.estimatedSavings ?? 0;
        return amt > 0;
    });

    return (
        <div className="flex flex-col gap-6 max-w-5xl mx-auto h-full">
            <h1 className="text-2xl font-medium">FinOps Intelligence</h1>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="p-6 rounded-xl bg-surface border border-border flex flex-col justify-center">
                    <div className="text-xs font-mono text-secondary uppercase tracking-wider mb-3 flex items-center gap-2">
                        <TrendingDown size={14} />
                        Estimated Monthly Savings
                    </div>
                    <div className="text-4xl font-semibold text-success">${totalSavings.toFixed(2)}</div>
                    <div className="text-sm text-secondary mt-2">SIMULATED ESTIMATE</div>
                </div>

                <div className="p-6 rounded-xl bg-surface border border-border flex flex-col justify-center">
                    <div className="text-xs font-mono text-secondary uppercase tracking-wider mb-3">Optimization Opportunities</div>
                    <div className="text-3xl font-medium">{savingsEvents.length}</div>
                    <div className="text-sm text-secondary mt-2">Successful optimizations</div>
                </div>

                <div className="p-6 rounded-xl bg-surface border border-border flex flex-col justify-center">
                    <div className="text-xs font-mono text-secondary uppercase tracking-wider mb-3">Direct Savings</div>
                    <div className="text-3xl font-medium text-success">${totalSavings.toFixed(2)}</div>
                    <div className="text-sm text-secondary mt-2">Calculated from execution</div>
                </div>
            </div>

            <div className="flex-1 bg-surface border border-border rounded-xl mt-4 flex flex-col overflow-hidden">
                <div className="p-5 border-b border-border flex justify-between items-center bg-subsurface">
                    <h2 className="text-sm font-mono uppercase tracking-wider">Savings History</h2>
                    <LineChart size={18} className="text-secondary" />
                </div>
                
                <div className="overflow-x-auto flex-1">
                    <table className="w-full text-left border-collapse whitespace-nowrap">
                        <thead className="bg-page/50 sticky top-0">
                            <tr>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider">Time</th>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider">Target</th>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider">Action</th>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider">AI Estimate</th>
                                <th className="p-4 text-xs font-medium text-secondary uppercase tracking-wider">FinOps Result</th>
                            </tr>
                        </thead>
                        <tbody className="text-sm">
                            {savingsEvents.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="text-center py-12 text-secondary">
                                        No optimization events recorded yet. Run an Idle VM simulation.
                                    </td>
                                </tr>
                            ) : (
                                savingsEvents.map((inc, i) => {
                                    const action = inc.action?.action || "UNKNOWN";
                                    const target = inc.action?.target || "UNKNOWN";
                                    const aiEstimate = inc.action?.estimatedSavings ?? 0;
                                    const finalSavings = inc.finOpsResult?.estimatedMonthlySavings ?? inc.estimatedSavings ?? 0;

                                    return (
                                        <tr key={i} className="border-b border-border/50 hover:bg-subsurface/50">
                                            <td className="p-4 font-mono text-secondary">{formatTimestamp(inc.timestamp)}</td>
                                            <td className="p-4 font-mono">{target}</td>
                                            <td className="p-4 text-xs font-mono bg-subsurface/50 rounded inline-block mt-3 mb-3 ml-4">{action}</td>
                                            <td className="p-4 font-mono text-secondary">{formatSavings(aiEstimate)}</td>
                                            <td className="p-4 font-mono text-success font-medium">+{formatSavings(finalSavings)}</td>
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
