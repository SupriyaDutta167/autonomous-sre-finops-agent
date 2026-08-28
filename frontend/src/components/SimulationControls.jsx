import { useState } from 'react';
import { 
    simulateCpuSpike, 
    simulateIdleVm, 
    simulateUnsafeAction, 
    simulateMemoryLeak, 
    simulateTrafficSurge 
} from '../services/api';

const SimulationControls = ({ onSimulationComplete }) => {
    const [loadingAction, setLoadingAction] = useState(null);
    const [error, setError] = useState(null);

    const handleSimulate = async (actionName, apiCall) => {
        setLoadingAction(actionName);
        setError(null);
        try {
            const result = await apiCall();
            if (onSimulationComplete) {
                onSimulationComplete(result);
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoadingAction(null);
        }
    };

    return (
        <div className="panel">
            <h2 className="panel-title">Simulation Controls</h2>
            
            <div className="grid grid-cols-2 gap-4">
                <button 
                    className="btn btn-primary"
                    disabled={loadingAction !== null}
                    onClick={() => handleSimulate('CPU SPIKE', simulateCpuSpike)}
                >
                    {loadingAction === 'CPU SPIKE' ? 'Simulating...' : 'CPU SPIKE'}
                </button>
                <button 
                    className="btn"
                    disabled={loadingAction !== null}
                    onClick={() => handleSimulate('MEMORY LEAK', simulateMemoryLeak)}
                >
                    {loadingAction === 'MEMORY LEAK' ? 'Simulating...' : 'MEMORY LEAK'}
                </button>
                <button 
                    className="btn"
                    disabled={loadingAction !== null}
                    onClick={() => handleSimulate('TRAFFIC SURGE', simulateTrafficSurge)}
                >
                    {loadingAction === 'TRAFFIC SURGE' ? 'Simulating...' : 'TRAFFIC SURGE'}
                </button>
                <button 
                    className="btn btn-primary"
                    disabled={loadingAction !== null}
                    onClick={() => handleSimulate('IDLE VM', simulateIdleVm)}
                >
                    {loadingAction === 'IDLE VM' ? 'Simulating...' : 'IDLE VM'}
                </button>
                <button 
                    className="btn btn-danger col-span-full"
                    disabled={loadingAction !== null}
                    onClick={() => handleSimulate('UNSAFE ACTION', simulateUnsafeAction)}
                >
                    {loadingAction === 'UNSAFE ACTION' ? 'Simulating...' : 'UNSAFE ACTION'}
                </button>
            </div>
            
            {error && (
                <div className="mt-4 p-2 bg-red-500/10 border border-red-500 rounded text-red-500 text-sm">
                    {error}
                </div>
            )}
        </div>
    );
};

export default SimulationControls;
