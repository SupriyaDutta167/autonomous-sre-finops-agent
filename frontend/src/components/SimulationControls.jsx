import { useState } from 'react';
import { useData } from '../contexts/DataContext';
import { 
    simulateCpuSpike, 
    simulateIdleVm, 
    simulateUnsafeAction, 
    simulateMemoryLeak, 
    simulateTrafficSurge 
} from '../services/api';
import { Cpu, MemoryStick, Activity, Moon, ShieldAlert, Loader2 } from 'lucide-react';
import { cn } from '../utils/cn';

const ActionButton = ({ name, icon: Icon, onClick, danger, isSimulating, loadingAction }) => {
    const loading = loadingAction === name;
    return (
        <button 
            className={cn(
                "flex flex-col items-center justify-center gap-3 p-4 rounded-xl border transition-all duration-200 relative overflow-hidden",
                danger 
                    ? "bg-danger/5 border-danger/20 text-danger hover:bg-danger/10 hover:border-danger/40" 
                    : "bg-surface border-border hover:bg-subsurface hover:border-secondary text-primary",
                (isSimulating && !loading) && "opacity-50 grayscale pointer-events-none"
            )}
            disabled={isSimulating}
            onClick={onClick}
        >
            {loading ? (
                <Loader2 size={24} className="animate-spin text-accent" />
            ) : (
                <Icon size={24} className={danger ? "text-danger" : "text-accent"} />
            )}
            <span className="text-sm font-medium tracking-wide">{name}</span>
        </button>
    );
};

export default function SimulationControls() {
    const { handleSimulationComplete } = useData();
    const [loadingAction, setLoadingAction] = useState(null);
    const [error, setError] = useState(null);

    const handleSimulate = async (actionName, apiCall) => {
        setLoadingAction(actionName);
        setError(null);
        try {
            const result = await apiCall();
            if (handleSimulationComplete) {
                handleSimulationComplete(result);
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoadingAction(null);
        }
    };

    const isSimulating = loadingAction !== null;

    return (
        <div className="flex flex-col gap-4">
            <h2 className="text-lg font-medium">Simulate Events</h2>
            
            <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
                <ActionButton 
                    name="CPU SPIKE" 
                    icon={Cpu} 
                    onClick={() => handleSimulate('CPU SPIKE', simulateCpuSpike)} 
                    isSimulating={isSimulating}
                    loadingAction={loadingAction}
                />
                <ActionButton 
                    name="MEMORY LEAK" 
                    icon={MemoryStick} 
                    onClick={() => handleSimulate('MEMORY LEAK', simulateMemoryLeak)} 
                    isSimulating={isSimulating}
                    loadingAction={loadingAction}
                />
                <ActionButton 
                    name="TRAFFIC SURGE" 
                    icon={Activity} 
                    onClick={() => handleSimulate('TRAFFIC SURGE', simulateTrafficSurge)} 
                    isSimulating={isSimulating}
                    loadingAction={loadingAction}
                />
                <ActionButton 
                    name="IDLE VM" 
                    icon={Moon} 
                    onClick={() => handleSimulate('IDLE VM', simulateIdleVm)} 
                    isSimulating={isSimulating}
                    loadingAction={loadingAction}
                />
                <ActionButton 
                    name="UNSAFE ACTION" 
                    icon={ShieldAlert} 
                    danger
                    onClick={() => handleSimulate('UNSAFE ACTION', simulateUnsafeAction)} 
                    isSimulating={isSimulating}
                    loadingAction={loadingAction}
                />
            </div>
            
            {error && (
                <div className="p-3 bg-danger/10 border border-danger/20 rounded-lg text-danger text-sm flex items-center gap-2">
                    <ShieldAlert size={16} />
                    {error}
                </div>
            )}
        </div>
    );
}
