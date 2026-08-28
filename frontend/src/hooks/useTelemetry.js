import { useState, useEffect, useCallback } from 'react';
import { getSimulationVms } from '../services/api';

export const useTelemetry = () => {
    const [vms, setVms] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchVms = useCallback(async () => {
        try {
            setLoading(true);
            const data = await getSimulationVms();
            setVms(data);
            setError(null);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        fetchVms();
    }, [fetchVms]);

    return { vms, loading, error, refreshVms: fetchVms };
};
