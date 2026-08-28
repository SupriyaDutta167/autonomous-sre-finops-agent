export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export const getHealth = async () => {
    const response = await fetch(`${API_BASE_URL}/api/health`);
    if (!response.ok) throw new Error("Backend unavailable");
    return response.json();
};

export const getIncidents = async () => {
    const response = await fetch(`${API_BASE_URL}/api/incidents`);
    if (!response.ok) throw new Error("Failed to fetch incidents");
    return response.json();
};

export const getSimulationVms = async () => {
    const response = await fetch(`${API_BASE_URL}/api/simulation/vms`);
    if (!response.ok) throw new Error("Failed to fetch VMs");
    return response.json();
};

export const simulateCpuSpike = async () => {
    const response = await fetch(`${API_BASE_URL}/api/simulate/cpu-spike`, { method: 'POST' });
    if (!response.ok) throw new Error("Failed to simulate CPU spike");
    return response.json();
};

export const simulateMemoryLeak = async () => {
    const response = await fetch(`${API_BASE_URL}/api/simulate/memory-leak`, { method: 'POST' });
    if (!response.ok) throw new Error("Failed to simulate memory leak");
    return response.json();
};

export const simulateTrafficSurge = async () => {
    const response = await fetch(`${API_BASE_URL}/api/simulate/traffic-surge`, { method: 'POST' });
    if (!response.ok) throw new Error("Failed to simulate traffic surge");
    return response.json();
};

export const simulateIdleVm = async () => {
    const response = await fetch(`${API_BASE_URL}/api/simulate/idle-vm`, { method: 'POST' });
    if (!response.ok) throw new Error("Failed to simulate idle VM");
    return response.json();
};

export const simulateUnsafeAction = async () => {
    const response = await fetch(`${API_BASE_URL}/api/simulate/unsafe-action`, { method: 'POST' });
    if (!response.ok) throw new Error("Failed to simulate unsafe action");
    return response.json();
};

export const sendAlert = async (alert) => {
    const response = await fetch(`${API_BASE_URL}/api/alerts`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(alert)
    });
    if (!response.ok) throw new Error("Failed to send alert");
    return response.json();
};
