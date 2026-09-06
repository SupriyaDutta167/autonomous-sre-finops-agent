import { useData } from '../contexts/DataContext';
import { formatTimestamp } from '../utils/formatters';

export default function Activity() {
    const { incidents } = useData();

    return (
        <div className="flex flex-col gap-6 max-w-5xl mx-auto h-full">
            <h1 className="text-2xl font-medium">Operations Log</h1>
            
            <div className="bg-[#0d1117] border border-border rounded-xl flex-1 flex flex-col font-mono text-sm overflow-hidden relative">
                <div className="absolute top-0 w-full h-8 bg-gradient-to-b from-[#0d1117] to-transparent z-10 pointer-events-none" />
                
                <div className="flex-1 overflow-y-auto p-6 pt-8 pb-8 custom-scrollbar">
                    {incidents.length === 0 ? (
                        <div className="text-secondary italic">Waiting for operations...</div>
                    ) : (
                        <div className="flex flex-col gap-6">
                            {incidents.map((inc, index) => {
                                const time = formatTimestamp(inc.timestamp);
                                const target = inc.action?.target || "UNKNOWN";
                                const action = inc.action?.action || "UNKNOWN";
                                const status = inc.status || inc.finalStatus || "UNKNOWN";
                                const decision = inc.decision?.status || inc.policyDecision?.status;
                                const exec = inc.executionResult?.executed ? "EXECUTED" : "NOT EXECUTED";

                                return (
                                    <div key={index} className="flex flex-col gap-1 border-l-2 border-border/50 pl-4 py-1">
                                        <div className="flex items-start gap-4">
                                            <span className="text-secondary w-20 shrink-0">{time}</span>
                                            <span className="text-accent w-28 shrink-0">DETECTED</span>
                                            <span className="text-primary">{target}</span>
                                        </div>
                                        <div className="flex items-start gap-4">
                                            <span className="text-secondary w-20 shrink-0"></span>
                                            <span className="text-accent w-28 shrink-0">AI PROPOSAL</span>
                                            <span className="text-primary">{action}</span>
                                        </div>
                                        <div className="flex items-start gap-4">
                                            <span className="text-secondary w-20 shrink-0"></span>
                                            <span className="text-accent w-28 shrink-0">POLICY</span>
                                            <span className={decision === 'BLOCKED' ? 'text-danger' : 'text-success'}>{decision || 'PENDING'}</span>
                                        </div>
                                        <div className="flex items-start gap-4">
                                            <span className="text-secondary w-20 shrink-0"></span>
                                            <span className="text-accent w-28 shrink-0">EXECUTION</span>
                                            <span className={exec === 'NOT EXECUTED' ? 'text-secondary' : 'text-primary'}>{exec}</span>
                                        </div>
                                        <div className="flex items-start gap-4">
                                            <span className="text-secondary w-20 shrink-0"></span>
                                            <span className="text-accent w-28 shrink-0">STATUS</span>
                                            <span className={status === 'BLOCKED' ? 'text-danger' : status === 'RESOLVED' ? 'text-success' : 'text-warning'}>
                                                {status}
                                            </span>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
