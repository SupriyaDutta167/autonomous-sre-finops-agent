import { useData } from '../contexts/DataContext';
import ClusterMetrics from '../components/ClusterMetrics';

export default function Infrastructure() {
    const { vms, loadingVms } = useData();

    return (
        <div className="flex flex-col gap-6 max-w-6xl mx-auto h-full">
            <div className="flex justify-between items-center">
                <h1 className="text-2xl font-medium">Infrastructure Inventory</h1>
                <div className="px-3 py-1 rounded bg-warning/10 text-warning text-xs font-mono uppercase tracking-wider border border-warning/20">
                    Simulation Mode
                </div>
            </div>
            
            <div className="flex-1 min-h-[400px]">
                <ClusterMetrics vms={vms} loading={loadingVms} />
            </div>
        </div>
    );
}
