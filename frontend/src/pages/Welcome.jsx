import { useNavigate } from 'react-router-dom';
import { ArrowRight, ShieldCheck, Cpu, LineChart } from 'lucide-react';

export default function Welcome() {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-page text-primary flex flex-col font-sans relative overflow-hidden">
            {/* Background elements */}
            <div className="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none z-0">
                <div className="absolute top-[-20%] left-[-10%] w-[50%] h-[50%] bg-accent/5 rounded-full blur-[120px]" />
                <div className="absolute bottom-[-20%] right-[-10%] w-[40%] h-[40%] bg-blue-500/5 rounded-full blur-[100px]" />
            </div>

            {/* Content */}
            <div className="flex-1 flex flex-col items-center justify-center p-6 z-10 relative">
                <div className="max-w-4xl w-full text-center space-y-8">
                    
                    <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-surface border border-border text-xs font-mono text-secondary mb-4 uppercase tracking-wider">
                        <span className="w-2 h-2 rounded-full bg-accent animate-pulse" />
                        System Initialization Ready
                    </div>
                    
                    <h1 className="text-4xl md:text-6xl font-semibold tracking-tight">
                        Autonomous SRE <span className="text-accent">&</span> FinOps Agent
                    </h1>
                    
                    <p className="text-lg md:text-xl text-secondary max-w-2xl mx-auto leading-relaxed">
                        Autonomous infrastructure reliability and cost optimization with AI-assisted decisions, deterministic safety controls, post-action verification, FinOps intelligence, and auditability.
                    </p>
                    
                    <div className="pt-8">
                        <button 
                            onClick={() => navigate('/overview')}
                            className="group inline-flex items-center gap-3 px-8 py-4 bg-primary text-page font-medium rounded-lg hover:bg-white transition-colors duration-250"
                        >
                            Enter Operations Center
                            <ArrowRight size={18} className="group-hover:translate-x-1 transition-transform" />
                        </button>
                    </div>
                </div>

                {/* Workflow UI representation */}
                <div className="w-full max-w-5xl mt-24 mb-16 overflow-x-auto pb-4 custom-scrollbar">
                    <div className="flex items-center justify-center gap-2 md:gap-4 min-w-max px-4">
                        {['DETECT', 'ANALYZE', 'DECIDE', 'POLICY', 'EXECUTE', 'VERIFY', 'FINOPS', 'AUDIT'].map((step, i) => (
                            <div key={step} className="flex items-center gap-2 md:gap-4">
                                <div className="px-4 py-2 bg-surface border border-border rounded text-xs font-mono text-secondary">
                                    {step}
                                </div>
                                {i < 7 && <ArrowRight size={14} className="text-border" />}
                            </div>
                        ))}
                    </div>
                </div>

                {/* Capability Blocks */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-5xl w-full">
                    <div className="p-6 rounded-xl bg-surface border border-border flex flex-col items-center text-center gap-4 hover:border-border/80 transition-colors">
                        <div className="w-12 h-12 rounded-lg bg-accent/10 text-accent flex items-center justify-center">
                            <Cpu size={24} />
                        </div>
                        <h3 className="font-medium text-lg">Autonomous SRE</h3>
                        <p className="text-secondary text-sm leading-relaxed">AI-driven analysis and automated remediation for infrastructure incidents and performance degradation.</p>
                    </div>
                    
                    <div className="p-6 rounded-xl bg-surface border border-border flex flex-col items-center text-center gap-4 hover:border-border/80 transition-colors">
                        <div className="w-12 h-12 rounded-lg bg-success/10 text-success flex items-center justify-center">
                            <LineChart size={24} />
                        </div>
                        <h3 className="font-medium text-lg">FinOps Intelligence</h3>
                        <p className="text-secondary text-sm leading-relaxed">Continuous cost optimization and waste reduction through intelligent scaling and idle resource management.</p>
                    </div>
                    
                    <div className="p-6 rounded-xl bg-surface border border-border flex flex-col items-center text-center gap-4 hover:border-border/80 transition-colors">
                        <div className="w-12 h-12 rounded-lg bg-warning/10 text-warning flex items-center justify-center">
                            <ShieldCheck size={24} />
                        </div>
                        <h3 className="font-medium text-lg">Safety First</h3>
                        <p className="text-secondary text-sm leading-relaxed">Deterministic policy engine guarantees all AI decisions are verified against organizational safety rules before execution.</p>
                    </div>
                </div>
            </div>
            
            {/* Footer */}
            <div className="w-full text-center py-6 text-xs text-secondary font-mono border-t border-border/50 bg-page/80 z-10">
                SYSTEM SIMULATION MODE // DO NOT USE FOR LIVE PRODUCTION // AUTHORIZED PERSONNEL ONLY
            </div>
        </div>
    );
}
