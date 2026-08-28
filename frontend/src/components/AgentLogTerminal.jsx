import { useEffect, useRef, useState } from 'react';
import { formatTimestamp } from '../utils/formatters';

const AgentLogTerminal = ({ normalizedData }) => {
    const [events, setEvents] = useState([]);
    const bottomRef = useRef(null);

    // Whenever a new incident is received, we generate terminal events for it
    useEffect(() => {
        if (!normalizedData) return;
        
        const timestamp = formatTimestamp(normalizedData.timestamp);
        const newEvents = [];
        
        // 1. Detect
        newEvents.push({ time: timestamp, type: 'DETECTED', text: normalizedData.alert?.instanceName || 'Unknown Alert' });
        
        // 2. AI Reasoning
        if (normalizedData.action) {
            newEvents.push({ time: timestamp, type: 'AI PROPOSAL', text: normalizedData.action.action });
        }
        
        // 3. Policy Guardrail
        if (normalizedData.decisionStatus) {
            newEvents.push({ time: timestamp, type: 'POLICY', text: normalizedData.decisionStatus });
        }
        
        // 4. Execution
        if (normalizedData.decisionStatus === 'BLOCKED' || normalizedData.decisionStatus === 'REQUIRES_APPROVAL') {
            newEvents.push({ time: timestamp, type: 'EXECUTION', text: 'NOT EXECUTED' });
        } else if (normalizedData.executionResult) {
            newEvents.push({ time: timestamp, type: 'EXECUTION', text: normalizedData.executionResult.success ? 'SUCCESS' : 'FAILED' });
        }
        
        // 5. Verification
        if (normalizedData.verificationResult) {
            newEvents.push({ time: timestamp, type: 'VERIFICATION', text: normalizedData.verificationResult.successful ? 'SUCCESS' : 'FAILED' });
        }
        
        // 6. FinOps
        if (normalizedData.decisionStatus === 'BLOCKED' || normalizedData.decisionStatus === 'REQUIRES_APPROVAL') {
             // For unsafe action, don't show FinOps log, or show $0, the prompt says: "For unsafe action: ... STATUS BLOCKED. Do not display FinOps". Wait, prompt says: "For successful CPU spike: FINOPS $0... For idle VM: FINOPS $150 estimated. For unsafe action: ... STATUS BLOCKED (no FinOps)".
             // So we just skip FinOps for blocked actions.
        } else if (normalizedData.savings > 0) {
            newEvents.push({ time: timestamp, type: 'FINOPS', text: `$${normalizedData.savings.toFixed(2)} estimated` });
        } else if (normalizedData.finalStatus) {
            newEvents.push({ time: timestamp, type: 'FINOPS', text: '$0 direct savings' });
        }
        
        // 7. Final
        if (normalizedData.finalStatus) {
            newEvents.push({ time: timestamp, type: 'STATUS', text: normalizedData.finalStatus });
        }
        newEvents.push({ time: timestamp, type: '---', text: '---' });

        // eslint-disable-next-line react-hooks/set-state-in-effect
        setEvents(prev => [...prev, ...newEvents]);
    }, [normalizedData]);

    // Auto scroll to bottom
    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [events]);

    return (
        <div className="panel h-full flex flex-col p-0">
            <h2 className="panel-title m-4 mb-2">Agent Event Log</h2>
            <div className="terminal flex-grow mx-4 mb-4">
                {events.length === 0 && (
                    <div className="text-secondary opacity-50">Waiting for agent events...</div>
                )}
                {events.map((event, idx) => (
                    <div key={idx} className="terminal-line">
                        {event.type !== '---' ? (
                            <>
                                <span className="terminal-time">[{event.time}]</span>
                                <span className="terminal-type">{event.type}</span>
                                <span className={event.text === 'FAILED' || event.text === 'BLOCKED' ? 'text-red-500' : 'text-slate-300'}>
                                    {event.text}
                                </span>
                            </>
                        ) : (
                            <span className="text-slate-600">----------------------------------------</span>
                        )}
                    </div>
                ))}
                <div ref={bottomRef} />
            </div>
        </div>
    );
};

export default AgentLogTerminal;
