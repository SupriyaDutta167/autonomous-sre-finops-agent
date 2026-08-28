const Header = ({ backendHealth }) => {
    return (
        <header className="panel flex justify-between items-center bg-slate-800 border-b border-slate-700" style={{ padding: '1rem 1.5rem', marginBottom: '0' }}>
            <div>
                <h1 className="text-xl font-semibold m-0" style={{ letterSpacing: '0.05em' }}>AUTONOMOUS SRE + FINOPS AGENT</h1>
                <div className="text-sm text-secondary mt-1">Autonomous infrastructure reliability & cost optimization</div>
            </div>
            <div className="flex items-center gap-6">
                <div className="flex flex-col items-end">
                    <span className="text-xs text-secondary mb-1">Backend</span>
                    {backendHealth?.status === 'healthy' ? (
                        <span className="status-badge status-approved">ONLINE</span>
                    ) : (
                        <span className="status-badge status-failed">UNAVAILABLE</span>
                    )}
                </div>
                <div className="flex flex-col items-end">
                    <span className="text-xs text-secondary mb-1">System</span>
                    <span className="status-badge status-approved">OPERATIONAL</span>
                </div>
            </div>
        </header>
    );
};

export default Header;
