import { useNavigate } from 'react-router-dom';

export default function ClusterMetrics({ vms, loading }) {
    const navigate = useNavigate();
    
    const renderProgressBar = (value) => {
        // Render ascii progress bar
        const blocks = Math.round(value / 10);
        const full = '█'.repeat(blocks);
        const empty = '░'.repeat(10 - blocks);
        return (
            <div className="flex items-center gap-2 w-32 font-mono text-xs">
                <span className={value > 85 ? 'text-danger' : 'text-accent'}>
                    {full}<span className="opacity-30">{empty}</span>
                </span>
                <span className="w-8 text-right">{value}%</span>
            </div>
        );
    };

    return (
        <div className="p-6 rounded-xl bg-surface border border-border h-full flex flex-col gap-4 overflow-hidden">
            <div className="flex justify-between items-center">
                <div className="text-xs font-mono text-secondary uppercase tracking-wider">Infrastructure State</div>
                {loading && <span className="text-xs text-accent animate-pulse font-mono uppercase">Syncing...</span>}
            </div>
            
            <div className="overflow-x-auto custom-scrollbar flex-1">
                <table className="w-full text-left border-collapse whitespace-nowrap">
                    <thead>
                        <tr className="border-b border-border">
                            <th className="pb-3 text-xs font-medium text-secondary uppercase tracking-wider">Instance</th>
                            <th className="pb-3 text-xs font-medium text-secondary uppercase tracking-wider">Status</th>
                            <th className="pb-3 text-xs font-medium text-secondary uppercase tracking-wider">CPU</th>
                            <th className="pb-3 text-xs font-medium text-secondary uppercase tracking-wider">Memory</th>
                            <th className="pb-3 text-xs font-medium text-secondary uppercase tracking-wider">Load</th>
                        </tr>
                    </thead>
                    <tbody className="text-sm">
                        {vms.length === 0 && !loading && (
                            <tr>
                                <td colSpan="5" className="text-center text-secondary py-8">
                                    No infrastructure data available
                                </td>
                            </tr>
                        )}
                        {vms.map((vm) => (
                            <tr 
                                key={vm.instanceName} 
                                className="border-b border-border/50 hover:bg-subsurface/50 transition-colors cursor-pointer group"
                                onClick={() => navigate(`/infrastructure/${vm.instanceName}`)}
                            >
                                <td className="py-3 font-mono text-accent group-hover:underline">{vm.instanceName}</td>
                                <td className="py-3">
                                    <div className="flex items-center gap-2">
                                        <div className={`w-1.5 h-1.5 rounded-full ${vm.state === 'RUNNING' ? 'bg-success' : 'bg-warning'}`} />
                                        <span className="text-xs uppercase">{vm.state}</span>
                                    </div>
                                </td>
                                <td className="py-3">{renderProgressBar(vm.cpuUtilization)}</td>
                                <td className="py-3">{renderProgressBar(vm.memoryUtilization)}</td>
                                <td className="py-3 font-mono text-secondary">
                                    {vm.requestRate}/s
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
