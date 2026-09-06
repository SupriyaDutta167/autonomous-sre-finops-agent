import { useParams, useNavigate } from 'react-router-dom';
import { useData } from '../contexts/DataContext';
import { ArrowLeft, Server, Activity, MemoryStick, Cpu, Database } from 'lucide-react';

const renderProgressBar = (value) => {
    return (
        <div className="flex flex-col gap-2 mt-2">
            <div className="w-full h-2 bg-subsurface rounded-full overflow-hidden">
                <div 
                    className={`h-full ${value > 85 ? 'bg-danger' : value > 60 ? 'bg-warning' : 'bg-success'}`}
                    style={{ width: `${Math.min(100, Math.max(0, value))}%` }}
                />
            </div>
            <div className="text-right text-xs font-mono text-secondary">{value}% utilized</div>
        </div>
    );
};

const MetricCard = ({ title, icon: Icon, value, secondary, bar }) => (
    <div className="p-6 rounded-xl bg-surface border border-border flex flex-col justify-center gap-2">
        <div className="flex items-center gap-2 text-xs font-mono text-secondary uppercase tracking-wider mb-2">
            <Icon size={14} />
            {title}
        </div>
        <div className="text-3xl font-medium">{value}</div>
        {secondary && <div className="text-sm text-secondary">{secondary}</div>}
        {bar !== undefined && renderProgressBar(bar)}
    </div>
);

export default function InfrastructureDetails() {
    const { instanceName } = useParams();
    const navigate = useNavigate();
    const { vms } = useData();

    const vm = vms.find(v => v.instanceName === instanceName);

    if (!vm) {
        return (
            <div className="flex flex-col items-center justify-center h-full gap-4">
                <div className="text-secondary">Infrastructure instance not found</div>
                <button onClick={() => navigate('/infrastructure')} className="text-accent hover:underline">
                    Return to Inventory
                </button>
            </div>
        );
    }

    return (
        <div className="flex flex-col gap-6 max-w-5xl mx-auto h-full">
            <button onClick={() => navigate('/infrastructure')} className="flex items-center gap-2 text-secondary hover:text-primary transition-colors self-start mb-2">
                <ArrowLeft size={16} />
                <span className="text-sm font-medium">Back to Inventory</span>
            </button>

            <div className="flex justify-between items-start border-b border-border pb-6">
                <div className="flex items-center gap-4">
                    <div className="w-16 h-16 rounded-xl bg-subsurface border border-border flex items-center justify-center text-primary">
                        <Server size={32} />
                    </div>
                    <div>
                        <h1 className="text-3xl font-medium font-mono text-accent">{vm.instanceName}</h1>
                        <div className="text-sm text-secondary mt-1">Environment: SIMULATION</div>
                    </div>
                </div>
                <div className={`flex items-center gap-2 px-4 py-2 rounded-lg border ${vm.state === 'RUNNING' ? 'bg-success/10 text-success border-success/20' : 'bg-warning/10 text-warning border-warning/20'}`}>
                    <div className={`w-2 h-2 rounded-full ${vm.state === 'RUNNING' ? 'bg-success' : 'bg-warning'}`} />
                    <span className="text-sm font-bold uppercase tracking-wider">{vm.state}</span>
                </div>
            </div>

            <h2 className="text-lg font-medium mt-4">Current State</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <MetricCard 
                    title="CPU Utilization" 
                    icon={Cpu} 
                    value={`${vm.cpuUtilization}%`} 
                    bar={vm.cpuUtilization}
                />
                <MetricCard 
                    title="Memory Utilization" 
                    icon={MemoryStick} 
                    value={`${vm.memoryUtilization}%`} 
                    bar={vm.memoryUtilization}
                />
                <MetricCard 
                    title="Request Rate" 
                    icon={Activity} 
                    value={`${vm.requestRate}/s`} 
                    secondary="Current load"
                />
                <MetricCard 
                    title="Capacity Limit" 
                    icon={Database} 
                    value={vm.capacity} 
                    secondary="Max concurrent requests"
                />
            </div>
            
            <div className="mt-8 p-4 border-l-4 border-accent bg-accent/5 rounded-r-lg">
                <h3 className="text-sm font-medium text-accent mb-1">Operational Note</h3>
                <p className="text-sm text-secondary">This infrastructure representation is simulated. Telemetry updates are generated locally for safe testing of the agent's remediation capabilities.</p>
            </div>
        </div>
    );
}
