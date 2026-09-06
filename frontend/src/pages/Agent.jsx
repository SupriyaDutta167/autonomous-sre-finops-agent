import { useData } from '../contexts/DataContext';
import AgentDecisionCard from '../components/AgentDecisionCard';
import { BrainCircuit, Activity, FileText, CheckCircle } from 'lucide-react';

export default function Agent() {
    const { latestIncident, simulatedResult } = useData();
    
    const rawData = simulatedResult || latestIncident;
    const action = rawData?.action;

    return (
        <div className="flex flex-col gap-8 max-w-4xl mx-auto h-full">
            <h1 className="text-2xl font-medium">AI Agent Control</h1>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <AgentDecisionCard action={action} />
                </div>
                
                <div className="p-6 rounded-xl bg-surface border border-border h-full">
                    <h2 className="text-xs font-mono text-secondary uppercase tracking-wider mb-6">Reasoning Pipeline</h2>
                    
                    <div className="flex flex-col items-center justify-center h-full max-h-[400px] -mt-6">
                        <div className="flex flex-col items-center w-full max-w-xs">
                            <div className="flex items-center gap-3 w-full p-3 rounded-lg border border-border bg-subsurface">
                                <Activity size={18} className="text-accent" />
                                <span className="text-sm font-mono tracking-wide">TELEMETRY</span>
                            </div>
                            
                            <div className="h-6 w-px bg-border my-1" />
                            
                            <div className="flex items-center gap-3 w-full p-3 rounded-lg border border-border bg-subsurface">
                                <BrainCircuit size={18} className="text-accent" />
                                <span className="text-sm font-mono tracking-wide">AI ANALYSIS</span>
                            </div>
                            
                            <div className="h-6 w-px bg-border my-1" />
                            
                            <div className="flex items-center gap-3 w-full p-3 rounded-lg border border-accent/30 bg-accent/10">
                                <FileText size={18} className="text-accent" />
                                <span className="text-sm font-mono tracking-wide text-accent">ACTION PROPOSAL</span>
                            </div>
                            
                            <div className="h-6 w-px bg-border my-1" />
                            
                            <div className="flex items-center gap-3 w-full p-3 rounded-lg border border-border bg-subsurface">
                                <CheckCircle size={18} className="text-secondary" />
                                <span className="text-sm font-mono tracking-wide">POLICY ENGINE</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
