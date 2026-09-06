import { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { API_BASE_URL } from '../services/api';

const PreferencesContext = createContext();

const PREF_KEY = 'sre-finops-preferences';

const defaultPrefs = { 
    theme: 'system', 
    sidebarCollapsed: false,
    runtimeMode: 'simulation',
    cloudMutations: false
};

export function PreferencesProvider({ children }) {
    // Default preferences
    const [preferences, setPreferences] = useState(() => {
        const stored = localStorage.getItem(PREF_KEY);
        if (stored) {
            try {
                return { ...defaultPrefs, ...JSON.parse(stored) };
            } catch (e) {
                console.error("Failed to parse preferences", e);
            }
        }
        return defaultPrefs;
    });

    const updatePreference = useCallback((key, value) => {
        setPreferences(prev => {
            const updated = { ...prev, [key]: value };
            localStorage.setItem(PREF_KEY, JSON.stringify(updated));
            
            // Sync specific keys to backend
            if (key === 'runtimeMode' || key === 'cloudMutations') {
                const payload = {};
                if (key === 'runtimeMode') payload.runtimeMode = value === 'google-cloud' ? 'GCP' : 'SIMULATION';
                if (key === 'cloudMutations') payload.mutationsEnabled = value;
                
                fetch(`${API_BASE_URL}/api/gcp/configure`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                }).catch(e => console.error("Failed to sync GCP config to backend", e));
            }
            return updated;
        });
    }, []);

    const resetPreferences = useCallback(() => {
        setPreferences(defaultPrefs);
        localStorage.setItem(PREF_KEY, JSON.stringify(defaultPrefs));
        
        fetch(`${API_BASE_URL}/api/gcp/configure`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ runtimeMode: 'SIMULATION', mutationsEnabled: false })
        }).catch(e => console.error("Failed to sync GCP config to backend", e));
    }, []);

    // Apply theme
    useEffect(() => {
        const applyTheme = (theme) => {
            const root = document.documentElement;
            if (theme === 'system') {
                const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
                root.setAttribute('data-theme', systemPrefersDark ? 'dark' : 'light');
            } else {
                root.setAttribute('data-theme', theme);
            }
        };

        applyTheme(preferences.theme);

        let mediaQuery;
        const handleChange = (e) => {
            if (preferences.theme === 'system') {
                document.documentElement.setAttribute('data-theme', e.matches ? 'dark' : 'light');
            }
        };

        if (preferences.theme === 'system') {
            mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
            mediaQuery.addEventListener('change', handleChange);
        }

        return () => {
            if (mediaQuery) {
                mediaQuery.removeEventListener('change', handleChange);
            }
        };
    }, [preferences.theme]);

    return (
        <PreferencesContext.Provider value={{ preferences, updatePreference, resetPreferences }}>
            {children}
        </PreferencesContext.Provider>
    );
}

// eslint-disable-next-line react-refresh/only-export-components
export const usePreferences = () => useContext(PreferencesContext);
