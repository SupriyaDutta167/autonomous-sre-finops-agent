import { useEffect, useState } from 'react';
import { getHealth } from '../services/api';

const SystemHealthOverview = ({ onHealthChange }) => {
    const [health, setHealth] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchHealth = async () => {
            try {
                setLoading(true);
                const data = await getHealth();
                setHealth(data);
                setError(null);
                if (onHealthChange) onHealthChange(data);
            } catch (err) {
                setError(err.message);
                if (onHealthChange) onHealthChange(null);
            } finally {
                setLoading(false);
            }
        };

        fetchHealth();
        const interval = setInterval(fetchHealth, 30000); // Check every 30s
        return () => clearInterval(interval);
    }, [onHealthChange]);

    return (
        <div className="panel flex flex-col justify-center h-full">
            <div className="metric-label mb-2">System Health</div>
            <div className="flex flex-col gap-2 mt-1">
                <div className="flex justify-between items-center">
                    <span className="text-xs text-secondary">Backend</span>
                    {health?.status === 'healthy' ? (
                        <span className="status-badge status-approved">ONLINE</span>
                    ) : (
                        <span className="status-badge status-failed">OFFLINE</span>
                    )}
                </div>
                <div className="flex justify-between items-center">
                    <span className="text-xs text-secondary">API</span>
                    {loading && !health ? (
                        <span className="status-badge status-detected">CHECKING</span>
                    ) : error ? (
                        <span className="status-badge status-failed">ERROR</span>
                    ) : (
                        <span className="status-badge status-approved">CONNECTED</span>
                    )}
                </div>
                <div className="flex justify-between items-center">
                    <span className="text-xs text-secondary">Engine</span>
                    {health?.simulationEngine === 'ready' || health?.status === 'healthy' ? (
                        <span className="status-badge status-approved">READY</span>
                    ) : (
                        <span className="status-badge status-failed">N/A</span>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SystemHealthOverview;
