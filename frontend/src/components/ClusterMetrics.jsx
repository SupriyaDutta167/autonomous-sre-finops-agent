
const ClusterMetrics = ({ vms, loading }) => {
    return (
        <div className="panel h-full">
            <div className="flex justify-between items-center mb-4">
                <h2 className="panel-title" style={{ border: 'none', margin: 0 }}>Cluster Infrastructure State</h2>
                {loading && <span className="text-xs text-secondary animate-pulse">Refreshing...</span>}
            </div>
            
            <div className="overflow-x-auto">
                <table>
                    <thead>
                        <tr>
                            <th>Instance Name</th>
                            <th>State</th>
                            <th>CPU</th>
                            <th>Memory</th>
                            <th>Requests</th>
                            <th>Capacity</th>
                        </tr>
                    </thead>
                    <tbody>
                        {vms.length === 0 && !loading && (
                            <tr>
                                <td colSpan="6" className="text-center text-secondary py-4">
                                    No infrastructure data available
                                </td>
                            </tr>
                        )}
                        {vms.map((vm) => (
                            <tr key={vm.instanceName}>
                                <td className="mono text-blue-400">{vm.instanceName}</td>
                                <td>
                                    <span className={`status-badge ${vm.state === 'RUNNING' ? 'status-approved' : 'status-detected'}`}>
                                        {vm.state}
                                    </span>
                                </td>
                                <td className={vm.cpuUtilization > 90 ? 'text-red-400' : ''}>
                                    {vm.cpuUtilization}%
                                </td>
                                <td>{vm.memoryUtilization}%</td>
                                <td>{vm.requestRate}/s</td>
                                <td>{vm.capacity}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ClusterMetrics;
