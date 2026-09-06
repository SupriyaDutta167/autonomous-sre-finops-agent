import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useData } from '../contexts/DataContext';
import { usePreferences } from '../contexts/PreferencesContext';
import { API_BASE_URL } from '../services/api';
import { Monitor, Moon, Sun, RotateCcw, AlertTriangle, CheckCircle2 } from 'lucide-react';
import { cn } from '../utils/cn';

const SettingsSection = ({ title, children }) => (
    <div className="mb-8">
        <h2 className="text-xs font-semibold tracking-wider text-secondary uppercase mb-4">{title}</h2>
        <div className="bg-surface border border-border rounded-xl overflow-hidden flex flex-col">
            {children}
        </div>
    </div>
);

const SettingRow = ({ label, value, valueClass, description, control }) => (
    <div className="flex flex-col sm:flex-row sm:items-center justify-between p-4 border-b border-border/50 hover:bg-subsurface/30 transition-colors last:border-0 gap-4 sm:gap-0">
        <div className="flex flex-col">
            <span className="font-medium text-sm text-primary">{label}</span>
            {description && <span className="text-xs text-secondary mt-1">{description}</span>}
        </div>
        {control ? control : <span className={cn("font-mono text-xs text-secondary", valueClass)}>{value}</span>}
    </div>
);

export default function Settings() {
    const { backendHealth } = useData();
    const { preferences, updatePreference, resetPreferences } = usePreferences();
    const location = useLocation();
    const navigate = useNavigate();
    
    const [dialogConfig, setDialogConfig] = useState(null);
    const [gcpStatus, setGcpStatus] = useState({ connected: false });
    const [gcpError, setGcpError] = useState(null);
    const [gcpSuccess, setGcpSuccess] = useState(false);
    const [testResult, setTestResult] = useState(null);

    const isConnected = backendHealth?.status === 'UP';

    const fetchGcpStatus = async () => {
        try {
            const res = await fetch(`${API_BASE_URL}/api/gcp/status`);
            if (res.ok) {
                const data = await res.json();
                setGcpStatus(data);
            }
        } catch {
            console.error("Failed to fetch GCP status");
        }
    };

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        fetchGcpStatus();
        
        const params = new URLSearchParams(location.search);
        if (params.get('error')) {
            setGcpError(params.get('error'));
            navigate('/settings', { replace: true });
        }
        if (params.get('gcp_success')) {
            setGcpSuccess(true);
            setTimeout(() => setGcpSuccess(false), 5000);
            navigate('/settings', { replace: true });
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [location.search, navigate]);

    const handleConnectGcp = () => {
        window.location.href = `${API_BASE_URL}/api/gcp/connect`;
    };

    const handleDisconnectGcp = async () => {
        try {
            await fetch(`${API_BASE_URL}/api/gcp/disconnect`, { method: 'POST' });
            await fetchGcpStatus();
        } catch {
            console.error("Disconnect failed");
        }
    };

    const handleTestConnection = async () => {
        setTestResult('TESTING...');
        try {
            const res = await fetch(`${API_BASE_URL}/api/gcp/test-connection`, { method: 'POST' });
            const data = await res.json();
            setTestResult(data.status);
            setTimeout(() => setTestResult(null), 3000);
        } catch {
            setTestResult('FAILED');
            setTimeout(() => setTestResult(null), 3000);
        }
    };

    const handleModeChange = (mode) => {
        if (mode === 'google-cloud' && preferences.runtimeMode !== 'google-cloud') {
            setDialogConfig({
                title: 'SWITCH TO GOOGLE CLOUD?',
                message: 'You are leaving Simulation Mode.\nCloud resources may be read from your configured Google Cloud environment.\n\nApproved mutations remain disabled unless explicitly enabled.',
                onConfirm: () => {
                    updatePreference('runtimeMode', 'google-cloud');
                    setDialogConfig(null);
                },
                confirmText: 'CONTINUE',
                confirmClass: 'bg-accent text-white hover:bg-accent/90'
            });
        } else {
            updatePreference('runtimeMode', 'simulation');
            updatePreference('cloudMutations', false);
        }
    };

    const handleMutationToggle = () => {
        if (!preferences.cloudMutations) {
            setDialogConfig({
                title: 'ENABLE CLOUD MUTATIONS?',
                message: 'This can allow approved autonomous actions to modify real Google Cloud infrastructure.',
                onConfirm: () => {
                    updatePreference('cloudMutations', true);
                    setDialogConfig(null);
                },
                confirmText: 'ENABLE',
                confirmClass: 'bg-danger text-white hover:bg-danger/90'
            });
        } else {
            updatePreference('cloudMutations', false);
        }
    };

    return (
        <div className="flex flex-col max-w-4xl mx-auto h-full pb-12 relative">
            <div className="mb-8">
                <h1 className="text-2xl font-medium mb-1">Settings</h1>
                <p className="text-secondary text-sm">Application configuration and preferences</p>
            </div>

            {gcpSuccess && (
                <div className="mb-6 p-4 bg-success/10 border border-success/30 rounded-lg flex items-center gap-3 text-success">
                    <CheckCircle2 size={20} />
                    <span className="text-sm font-medium">Successfully connected to Google Cloud!</span>
                </div>
            )}
            
            {gcpError && (
                <div className="mb-6 p-4 bg-danger/10 border border-danger/30 rounded-lg flex items-center gap-3 text-danger">
                    <AlertTriangle size={20} />
                    <span className="text-sm font-medium">GCP Connection Error: {gcpError}</span>
                </div>
            )}
            
            <SettingsSection title="Runtime">
                <div className="p-4 border-b border-border/50">
                    <label className="text-sm font-medium text-primary mb-3 block">RUNTIME MODE</label>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <button 
                            onClick={() => handleModeChange('simulation')}
                            className={cn("flex flex-col p-4 border rounded-lg text-left transition-colors", preferences.runtimeMode === 'simulation' ? "border-accent bg-accent/5 ring-1 ring-accent" : "border-border hover:border-secondary")}
                        >
                            <span className={cn("font-medium mb-1", preferences.runtimeMode === 'simulation' ? "text-accent" : "text-primary")}>SIMULATION</span>
                            <span className="text-xs text-secondary">Safe local infrastructure simulation.</span>
                        </button>
                        <button 
                            onClick={() => handleModeChange('google-cloud')}
                            className={cn("flex flex-col p-4 border rounded-lg text-left transition-colors", preferences.runtimeMode === 'google-cloud' ? "border-accent bg-accent/5 ring-1 ring-accent" : "border-border hover:border-secondary")}
                        >
                            <span className={cn("font-medium mb-1", preferences.runtimeMode === 'google-cloud' ? "text-accent" : "text-primary")}>GOOGLE CLOUD</span>
                            <span className="text-xs text-secondary">Connect to a Google Cloud environment.</span>
                        </button>
                    </div>
                </div>

                <SettingRow label="Provider" value={preferences.runtimeMode === 'simulation' ? "Simulation" : "Google Cloud"} />
                <SettingRow label="Connection" value={isConnected ? "CONNECTED" : "NOT CONNECTED"} valueClass={isConnected ? "text-success font-bold" : "text-danger font-bold"} />
                <SettingRow label="Mutation State" value={preferences.cloudMutations ? "ENABLED" : "DISABLED"} valueClass={preferences.cloudMutations ? "text-danger font-bold" : "text-secondary"} />
            </SettingsSection>

            {preferences.runtimeMode === 'google-cloud' && (
                <SettingsSection title="Cloud Connection">
                    <SettingRow 
                        label="Project ID" 
                        value={gcpStatus.connected ? (gcpStatus.projectId || "Not Selected") : "---"} 
                    />
                    <SettingRow 
                        label="Zone" 
                        value={gcpStatus.connected ? (gcpStatus.zone || "Not Selected") : "---"} 
                    />
                    <SettingRow 
                        label="Authentication" 
                        value={gcpStatus.connected ? "GOOGLE CLOUD CONNECTED" : "GOOGLE CLOUD NOT CONNECTED"} 
                        valueClass={gcpStatus.connected ? "text-success font-bold" : "text-warning font-bold"}
                        description={gcpStatus.connected ? "Authenticated via OAuth." : "You will be redirected to Google to authenticate and grant this application access to your cloud resources."}
                    />
                    <div className="p-4 border-b border-border/50 flex gap-4">
                        {!gcpStatus.connected ? (
                            <button className="btn-base px-4 py-2 border border-border text-sm hover:bg-subsurface transition-colors" onClick={handleConnectGcp}>
                                CONNECT GOOGLE CLOUD
                            </button>
                        ) : (
                            <button className="btn-base px-4 py-2 border border-danger text-danger text-sm hover:bg-danger/10 transition-colors" onClick={handleDisconnectGcp}>
                                DISCONNECT GOOGLE CLOUD
                            </button>
                        )}
                        <button 
                            className="btn-base px-4 py-2 border border-border text-sm hover:bg-subsurface transition-colors disabled:opacity-50" 
                            onClick={handleTestConnection}
                            disabled={!gcpStatus.connected}
                        >
                            {testResult || "TEST CONNECTION"}
                        </button>
                    </div>
                </SettingsSection>
            )}

            <SettingsSection title="Execution Safety">
                {preferences.runtimeMode === 'google-cloud' ? (
                    <div className="flex flex-col border-b border-border/50">
                        <div className={cn("p-4 flex items-center gap-3", preferences.cloudMutations ? "bg-danger/10 text-danger border-b border-danger/20" : "bg-success/10 text-success border-b border-success/20")}>
                            <AlertTriangle size={20} />
                            <span className="font-semibold text-sm">
                                {preferences.cloudMutations ? "LIVE CLOUD MUTATIONS ENABLED" : "SAFE CLOUD MODE"}
                            </span>
                        </div>
                        <div className="p-4">
                            <p className="text-sm text-secondary mb-4">
                                {preferences.cloudMutations 
                                    ? "Approved autonomous actions may affect real Google Cloud infrastructure." 
                                    : "Cloud state may be inspected, but autonomous mutations are disabled."}
                            </p>
                            <div className="flex items-center justify-between p-4 border border-border rounded-lg bg-subsurface/30">
                                <div className="flex flex-col">
                                    <span className="font-medium text-sm">CLOUD MUTATIONS</span>
                                    <span className="text-xs text-secondary mt-1">When ON, approved actions may modify resources.</span>
                                </div>
                                <button 
                                    onClick={handleMutationToggle}
                                    className={cn("relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-accent focus:ring-offset-2 focus:ring-offset-page", preferences.cloudMutations ? "bg-danger" : "bg-border")}
                                    role="switch"
                                    aria-checked={preferences.cloudMutations}
                                    aria-label="Toggle cloud mutations"
                                >
                                    <span className={cn("inline-block h-4 w-4 transform rounded-full bg-white transition-transform", preferences.cloudMutations ? "translate-x-6" : "translate-x-1")} />
                                </button>
                            </div>
                        </div>
                    </div>
                ) : (
                    <div className="p-4 flex items-center gap-3 bg-success/10 text-success">
                        <Monitor size={20} />
                        <span className="font-semibold text-sm">SIMULATION MODE</span>
                    </div>
                )}
            </SettingsSection>

            <SettingsSection title="AI Configuration">
                <SettingRow label="AI Provider" value="Gemini / Mock" />
                <SettingRow label="Reasoning Mode" value="Configured provider" />
            </SettingsSection>

            <SettingsSection title="Appearance">
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-0 sm:divide-x divide-y sm:divide-y-0 divide-border">
                    <button
                        onClick={() => updatePreference('theme', 'dark')}
                        className={cn("flex flex-col p-4 text-left transition-colors hover:bg-subsurface", preferences.theme === 'dark' ? "bg-accent/5 ring-1 ring-inset ring-accent" : "")}
                    >
                        <div className="flex items-center gap-3 mb-2">
                            <Moon size={18} className={preferences.theme === 'dark' ? 'text-accent' : 'text-secondary'} />
                            <span className={cn("font-medium", preferences.theme === 'dark' ? "text-accent" : "text-primary")}>
                                Dark {preferences.theme === 'dark' && "✓"}
                            </span>
                        </div>
                        <span className="text-xs text-secondary">Best for operations environments.</span>
                    </button>
                    
                    <button
                        onClick={() => updatePreference('theme', 'light')}
                        className={cn("flex flex-col p-4 text-left transition-colors hover:bg-subsurface", preferences.theme === 'light' ? "bg-accent/5 ring-1 ring-inset ring-accent" : "")}
                    >
                        <div className="flex items-center gap-3 mb-2">
                            <Sun size={18} className={preferences.theme === 'light' ? 'text-accent' : 'text-secondary'} />
                            <span className={cn("font-medium", preferences.theme === 'light' ? "text-accent" : "text-primary")}>
                                Light {preferences.theme === 'light' && "✓"}
                            </span>
                        </div>
                        <span className="text-xs text-secondary">High-contrast light workspace.</span>
                    </button>
                    
                    <button
                        onClick={() => updatePreference('theme', 'system')}
                        className={cn("flex flex-col p-4 text-left transition-colors hover:bg-subsurface", preferences.theme === 'system' ? "bg-accent/5 ring-1 ring-inset ring-accent" : "")}
                    >
                        <div className="flex items-center gap-3 mb-2">
                            <Monitor size={18} className={preferences.theme === 'system' ? 'text-accent' : 'text-secondary'} />
                            <span className={cn("font-medium", preferences.theme === 'system' ? "text-accent" : "text-primary")}>
                                System {preferences.theme === 'system' && "✓"}
                            </span>
                        </div>
                        <span className="text-xs text-secondary">Follow operating-system preference.</span>
                    </button>
                </div>
            </SettingsSection>

            <SettingsSection title="Data">
                <SettingRow label="Incident History" value="IN-MEMORY" />
                <SettingRow label="Simulation State" value="IN-MEMORY" />
                <div className="p-4 border-t border-border/50 bg-subsurface/30">
                    <p className="text-xs text-secondary">Data resets when the backend restarts. Persistent storage is not configured in simulation mode.</p>
                </div>
            </SettingsSection>

            <div className="mt-8 pt-4">
                <button 
                    onClick={resetPreferences}
                    className="flex items-center gap-2 text-sm text-danger hover:text-danger/80 transition-colors font-medium"
                >
                    <RotateCcw size={16} />
                    RESET PREFERENCES
                </button>
                <p className="text-xs text-secondary mt-2 max-w-md">
                    Restores default appearance, sidebar preferences, and resets runtime back to safe simulation mode.
                </p>
            </div>

            {/* Confirmation Dialog Overlay */}
            {dialogConfig && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-page/80 backdrop-blur-sm">
                    <div className="bg-surface border border-border rounded-xl shadow-xl max-w-md w-full overflow-hidden flex flex-col animate-in fade-in zoom-in duration-200">
                        <div className="p-6">
                            <h3 className="text-lg font-medium text-primary mb-4">{dialogConfig.title}</h3>
                            <p className="text-sm text-secondary whitespace-pre-line">{dialogConfig.message}</p>
                        </div>
                        <div className="p-4 bg-subsurface border-t border-border flex justify-end gap-3">
                            <button 
                                className="px-4 py-2 rounded text-sm font-medium text-secondary hover:text-primary transition-colors focus:outline-none focus:ring-2 focus:ring-border"
                                onClick={() => setDialogConfig(null)}
                            >
                                CANCEL
                            </button>
                            <button 
                                className={cn("px-4 py-2 rounded text-sm font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-page", dialogConfig.confirmClass)}
                                onClick={dialogConfig.onConfirm}
                            >
                                {dialogConfig.confirmText}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
