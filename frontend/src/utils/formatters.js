export const formatTimestamp = (isoString) => {
    if (!isoString) return "N/A";
    const date = new Date(isoString);
    return date.toLocaleTimeString([], { hour12: false });
};

export const formatConfidence = (value) => {
    if (value === undefined || value === null) return "N/A";
    return `${(value * 100).toFixed(0)}%`;
};

export const formatSavings = (value) => {
    if (value === undefined || value === null) return "$0";
    return `$${value.toFixed(2)}`;
};

export const formatStatus = (status) => {
    return status ? status.replace(/_/g, ' ') : "UNKNOWN";
};
