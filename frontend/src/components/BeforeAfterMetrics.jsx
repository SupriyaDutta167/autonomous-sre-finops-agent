
const BeforeAfterMetrics = ({ executionResult, verificationResult, decisionStatus }) => {
    
    const isBlocked = decisionStatus === 'BLOCKED' || decisionStatus === 'REQUIRES_APPROVAL';
    
    let execStatus = "PENDING";
    let execColor = "text-gray-400";
    let execIcon = "";
    
    if (isBlocked) {
        execStatus = "NOT EXECUTED";
        execIcon = "✕ ";
    } else if (executionResult) {
        execStatus = executionResult.success ? "SUCCESS" : "FAILED";
        execColor = executionResult.success ? "text-green-400" : "text-red-500";
        execIcon = executionResult.success ? "✓ " : "✕ ";
    }

    let verStatus = "PENDING";
    let verColor = "text-gray-400";
    let verMessage = "";
    let verIcon = "";

    if (isBlocked) {
        verStatus = "N/A";
    } else if (verificationResult) {
        verStatus = verificationResult.successful ? "SUCCESS" : "FAILED";
        verColor = verificationResult.successful ? "text-green-400" : "text-red-500";
        verMessage = verificationResult.message;
        verIcon = verificationResult.successful ? "✓ " : "✕ ";
    }

    return (
        <div className="panel h-full">
            <h2 className="panel-title">Execution & Verification</h2>
            
            <div className="grid grid-cols-2 gap-4 h-full items-center">
                <div className="text-center p-4 border border-slate-700 rounded bg-slate-800/50">
                    <div className="metric-label mb-2">Execution</div>
                    <div className={`text-xl font-semibold ${execColor}`}>
                        {execIcon}{execStatus}
                    </div>
                    {executionResult && executionResult.message && (
                        <div className="text-sm text-secondary mt-2">{executionResult.message}</div>
                    )}
                </div>
                
                <div className="text-center p-4 border border-slate-700 rounded bg-slate-800/50">
                    <div className="metric-label mb-2">Verification</div>
                    <div className={`text-xl font-semibold ${verColor}`}>
                        {verIcon}{verStatus}
                    </div>
                    {verMessage && (
                        <div className="text-sm text-secondary mt-2">{verMessage}</div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default BeforeAfterMetrics;
