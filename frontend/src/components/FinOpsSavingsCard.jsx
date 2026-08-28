import { formatSavings } from '../utils/formatters';

const FinOpsSavingsCard = ({ savings = 0, decisionStatus, isPending = false }) => {
    
    let displaySavings = savings;
    
    // As per accounting boundaries, if blocked/failed, savings are $0.
    if (decisionStatus === 'BLOCKED' || decisionStatus === 'REQUIRES_APPROVAL' || decisionStatus === 'FAILED') {
        displaySavings = 0;
    }

    return (
        <div className="panel flex flex-col justify-center h-full">
            <div className="metric-label mb-2">Est. Savings</div>
            <div className="text-2xl font-semibold text-green-400">
                {formatSavings(displaySavings)}
            </div>
            
            {displaySavings > 0 ? (
                <div className="text-xs text-yellow-500 mt-1 uppercase tracking-wider">
                    Simulated Estimate
                </div>
            ) : (
                <div className="text-xs text-secondary mt-1">
                    {isPending ? "Pending execution" : "$0 direct savings"}
                </div>
            )}
        </div>
    );
};

export default FinOpsSavingsCard;
