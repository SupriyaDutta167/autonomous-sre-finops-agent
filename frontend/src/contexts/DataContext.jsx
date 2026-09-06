/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { getIncidents, getSimulationVms, getHealth } from '../services/api';

const DataContext = createContext(null);

export const DataProvider = ({ children }) => {
    const [incidents, setIncidents] = useState([]);
    const [vms, setVms] = useState([]);
    const [backendHealth, setBackendHealth] = useState(null);
    
    const [loadingIncidents, setLoadingIncidents] = useState(true);
    const [loadingVms, setLoadingVms] = useState(true);
    const [error, setError] = useState(null);

    const [simulatedResult, setSimulatedResult] = useState(null);

    const fetchIncidents = useCallback(async (isInitial = false) => {
        try {
            if (isInitial) setLoadingIncidents(true);
            const data = await getIncidents();
            const sortedData = [...data].sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
            setIncidents(sortedData);
            setError(null);
        } catch (errorObj) {
            setError(errorObj.message);
        } finally {
            if (isInitial) setLoadingIncidents(false);
        }
    }, []);

    const fetchVms = useCallback(async () => {
        try {
            const data = await getSimulationVms();
            setVms(data);
            setError(null);
        } catch (errorObj) {
            setError(errorObj.message);
        } finally {
            setLoadingVms(false);
        }
    }, []);

    const fetchHealth = useCallback(async () => {
        try {
            const data = await getHealth();
            setBackendHealth(data);
        } catch {
            setBackendHealth({ status: 'DOWN' });
        }
    }, []);

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        fetchIncidents(true);
        fetchVms();
        fetchHealth();

        const interval = setInterval(() => {
            fetchIncidents(false);
            fetchVms();
            fetchHealth();
        }, 10000);

        return () => clearInterval(interval);
    }, [fetchIncidents, fetchVms, fetchHealth]);

    const handleSimulationComplete = (result) => {
        setSimulatedResult(result);
        fetchVms();
        fetchIncidents();
    };

    const latestIncident = incidents.length > 0 ? incidents[0] : null;

    return (
        <DataContext.Provider value={{
            incidents,
            latestIncident,
            vms,
            backendHealth,
            loadingIncidents,
            loadingVms,
            error,
            simulatedResult,
            handleSimulationComplete,
            refreshData: () => {
                fetchIncidents();
                fetchVms();
                fetchHealth();
            }
        }}>
            {children}
        </DataContext.Provider>
    );
};

export const useData = () => {
    const context = useContext(DataContext);
    if (!context) {
        throw new Error("useData must be used within a DataProvider");
    }
    return context;
};
