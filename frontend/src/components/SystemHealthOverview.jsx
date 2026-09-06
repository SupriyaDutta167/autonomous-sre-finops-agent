import { useData } from '../contexts/DataContext';

export default function SystemHealthOverview() {
    const { backendHealth } = useData();
    
    const isOnline = backendHealth?.status === 'healthy';

    return (
        <div className="p-5 rounded-xl bg-surface border border-border flex flex-col h-full justify-between gap-4">
            <div className="text-xs font-mono text-secondary uppercase tracking-wider">System Status</div>
            
            <div className="flex flex-col gap-3">
                <div className="flex justify-between items-center">
                    <span className="text-sm text-secondary">Control Plane</span>
                    {isOnline ? (
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold tracking-wider bg-success/10 text-success border border-success/20">ONLINE</span>
                    ) : (
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold tracking-wider bg-danger/10 text-danger border border-danger/20">OFFLINE</span>
                    )}
                </div>
                
                <div className="flex justify-between items-center">
                    <span className="text-sm text-secondary">Policy Engine</span>
                    {backendHealth?.simulationEngine === 'ready' || isOnline ? (
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold tracking-wider bg-success/10 text-success border border-success/20">READY</span>
                    ) : (
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold tracking-wider bg-warning/10 text-warning border border-warning/20">PENDING</span>
                    )}
                </div>
                
                <div className="flex justify-between items-center">
                    <span className="text-sm text-secondary">GCP Adapter</span>
                    {isOnline ? (
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold tracking-wider bg-accent/10 text-accent border border-accent/20">CONNECTED</span>
                    ) : (
                        <span className="px-2 py-0.5 rounded text-[10px] font-bold tracking-wider bg-danger/10 text-danger border border-danger/20">DISCONNECTED</span>
                    )}
                </div>
            </div>
        </div>
    );
}
