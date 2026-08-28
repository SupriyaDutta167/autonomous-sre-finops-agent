import { useState, useEffect, useCallback } from 'react';
import { getIncidents } from '../services/api';

export const useIncidentStream = () => {
    const [incidents, setIncidents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchIncidents = useCallback(async (isInitial = false) => {
        try {
            if (isInitial) setLoading(true);
            const data = await getIncidents();
            // Data is expected to be an array, sort by timestamp descending
            const sortedData = [...data].sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
            setIncidents(sortedData);
            setError(null);
        } catch (err) {
            setError(err.message);
        } finally {
            if (isInitial) setLoading(false);
        }
    }, []);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        fetchIncidents(true);
        
        // Lightweight polling (e.g., every 10 seconds)
        const interval = setInterval(() => fetchIncidents(false), 10000);
        return () => clearInterval(interval);
    }, [fetchIncidents]);

    return { 
        incidents, 
        latestIncident: incidents.length > 0 ? incidents[0] : null,
        loading, 
        error, 
        refreshIncidents: fetchIncidents 
    };
};
