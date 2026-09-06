import { useData } from '../contexts/DataContext';
import { API_BASE_URL } from '../services/api';
import { Settings2, Database, Shield, Box, Server, Bot } from 'lucide-react';

const SettingRow = ({ icon: Icon, label, value, status }) => (
    <div className="flex items-center justify-between p-4 border-b border-border/50 hover:bg-subsurface/30 transition-colors last:border-0">
        <div className="flex items-center gap-4">
            <div className="w-10 h-10 rounded-lg bg-subsurface border border-border flex items-center justify-center text-secondary">
                <Icon size={20} />
            </div>
            <span className="font-medium text-sm">{label}</span>
        </div>
        <div className="flex items-center gap-3">
            <span className="font-mono text-xs text-secondary">{value}</span>
            {status && (
                <span className={`px-2 py-1 rounded text-[10px] font-bold tracking-wider border ${
                    status === 'good' ? 'bg-success/10 text-success border-success/20' : 
                    status === 'warn' ? 'bg-warning/10 text-warning border-warning/20' : 
                    'bg-secondary/10 text-secondary border-secondary/20'
                }`}>
                    {status === 'good' ? 'ACTIVE' : status === 'warn' ? 'PENDING' : 'DISABLED'}
                </span>
            )}
        </div>
    </div>
);

export default function Settings() {
    const { backendHealth } = useData();

    const isConnected = backendHealth?.status === 'healthy';

    return (
        <div className="flex flex-col gap-6 max-w-4xl mx-auto h-full">
            <h1 className="text-2xl font-medium mb-2">System Configuration</h1>
            
            <div className="bg-surface border border-border rounded-xl overflow-hidden flex flex-col">
                <div className="p-4 bg-subsurface border-b border-border">
                    <h2 className="text-sm font-medium">Runtime Environment</h2>
                </div>
                <div className="flex flex-col">
                    <SettingRow 
                        icon={Settings2} 
                        label="Runtime Mode" 
                        value="SIMULATION" 
                        status="warn" 
                    />
                    <SettingRow 
                        icon={Database} 
                        label="Backend Control Plane" 
                        value={isConnected ? "ONLINE" : "UNAVAILABLE"} 
                        status={isConnected ? "good" : "error"} 
                    />
                    <SettingRow 
                        icon={Server} 
                        label="API Base URL" 
                        value={API_BASE_URL} 
                    />
                </div>
            </div>

            <div className="bg-surface border border-border rounded-xl overflow-hidden flex flex-col">
                <div className="p-4 bg-subsurface border-b border-border">
                    <h2 className="text-sm font-medium">Integrations</h2>
                </div>
                <div className="flex flex-col">
                    <SettingRow 
                        icon={Bot} 
                        label="AI Provider" 
                        value="Google Gemini Pro" 
                        status="good" 
                    />
                    <SettingRow 
                        icon={Box} 
                        label="GCP Adapter" 
                        value="AVAILABLE" 
                        status="good" 
                    />
                    <SettingRow 
                        icon={Shield} 
                        label="GCP Live Mutations" 
                        value="RESTRICTED" 
                        status="inactive" 
                    />
                </div>
            </div>
            
            <div className="mt-8 p-4 bg-accent/5 border border-accent/20 rounded-lg text-sm text-secondary flex items-start gap-3">
                <Shield className="text-accent shrink-0 mt-0.5" size={18} />
                <p>The control plane is currently running in simulation mode. Live GCP mutations are disabled by default for safety. Policies are evaluated against simulated telemetry.</p>
            </div>
        </div>
    );
}
