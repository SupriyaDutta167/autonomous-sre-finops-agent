import { useState } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { LayoutDashboard, AlertCircle, Server, BrainCircuit, ShieldAlert, DollarSign, ActivitySquare, Settings, Menu, X } from 'lucide-react';
import { cn } from '../utils/cn';
import { useData } from '../contexts/DataContext';

const NAV_ITEMS = [
    { name: 'Overview', path: '/overview', icon: LayoutDashboard },
    { name: 'Incidents', path: '/incidents', icon: AlertCircle },
    { name: 'Infrastructure', path: '/infrastructure', icon: Server },
    { name: 'AI Agent', path: '/agent', icon: BrainCircuit },
    { name: 'Safety & Policies', path: '/safety', icon: ShieldAlert },
    { name: 'FinOps', path: '/finops', icon: DollarSign },
    { name: 'Activity', path: '/activity', icon: ActivitySquare },
    { name: 'Settings', path: '/settings', icon: Settings },
];

import { usePreferences } from '../contexts/PreferencesContext';

const NavContent = ({ sidebarOpen, setSidebarOpen, setMobileOpen, isMobileDrawer = false, preferences }) => (
    <>
        {/* Header Rail (h-16 = 64px) */}
        <div className="flex h-16 items-center px-4 shrink-0">
            {/* Hamburger perfectly centered in w-16 (64px). px-4(16) + p-1.5(6) = 22px left edge. 20px icon. Center = 32px */}
            <button 
                className="text-secondary hover:text-primary transition-colors focus:outline-none focus:ring-2 focus:ring-accent rounded p-1.5 shrink-0"
                aria-label="Toggle navigation"
                onClick={() => {
                    if (isMobileDrawer) {
                        setMobileOpen(false);
                    } else {
                        setSidebarOpen(!sidebarOpen);
                    }
                }}
            >
                <Menu size={20} />
            </button>
            <div className={cn("flex items-center gap-3 overflow-hidden whitespace-nowrap transition-all duration-250 ml-2.5", sidebarOpen ? "w-full opacity-100" : "w-0 opacity-0")}>
                <div className="flex items-center justify-center w-8 h-8 rounded bg-accent/10 text-accent shrink-0">
                    <BrainCircuit size={18} />
                </div>
                <span className="font-semibold tracking-wide text-primary">
                    AGENT<span className="text-accent">OS</span>
                </span>
            </div>
        </div>

        {/* Navigation Rail */}
        <div className="flex-1 overflow-y-auto py-4 flex flex-col gap-1 px-3">
            {NAV_ITEMS.map((item) => (
                <NavLink
                    key={item.path}
                    to={item.path}
                    onClick={() => setMobileOpen(false)}
                    className={({ isActive }) => cn(
                        "flex items-center gap-3 px-2.5 py-2.5 rounded-lg transition-colors group", // px-3(12) + px-2.5(10) = 22px left edge. Center = 32px
                        isActive 
                            ? "bg-accent/10 text-accent font-medium" 
                            : "text-secondary hover:text-primary hover:bg-subsurface"
                    )}
                    title={!sidebarOpen ? item.name : undefined}
                >
                    <item.icon size={20} className="shrink-0" />
                    <span className={cn("whitespace-nowrap transition-all duration-250", !sidebarOpen ? "opacity-0 lg:w-0 overflow-hidden" : "opacity-100")}>
                        {item.name}
                    </span>
                </NavLink>
            ))}
        </div>

        {/* Bottom Status Rail */}
        <div className="p-3 mt-auto">
            <div className={cn("flex rounded-lg bg-subsurface overflow-hidden whitespace-nowrap transition-all duration-250", !sidebarOpen ? "justify-center items-center w-10 h-10 mx-auto" : "flex-col gap-2 p-3 w-full")}>
                <div className="flex items-center gap-2">
                    <div className={cn("w-2 h-2 rounded-full animate-pulse shrink-0", 
                        preferences.runtimeMode === 'simulation' ? "bg-warning" : 
                        preferences.cloudMutations ? "bg-danger" : "bg-success")} 
                    />
                    <span className={cn("text-xs font-medium uppercase", 
                        preferences.runtimeMode === 'simulation' ? "text-warning" : 
                        preferences.cloudMutations ? "text-danger" : "text-success",
                        !sidebarOpen ? "opacity-0 lg:w-0 overflow-hidden hidden" : "opacity-100")}>
                        {preferences.runtimeMode === 'simulation' ? 'Simulation Mode' : 
                         preferences.cloudMutations ? 'Live Mutations' : 'Safe Cloud Mode'}
                    </span>
                </div>
            </div>
        </div>
    </>
);

export default function AppLayout() {
    const { preferences, updatePreference } = usePreferences();
    const sidebarOpen = !preferences.sidebarCollapsed;
    const setSidebarOpen = (open) => updatePreference('sidebarCollapsed', !open);
    
    const [mobileOpen, setMobileOpen] = useState(false);
    const { backendHealth } = useData();
    const location = useLocation();

    // Determine current page title
    const currentPage = NAV_ITEMS.find(item => location.pathname.startsWith(item.path))?.name || 'Overview';

    const isConnected = backendHealth?.status === 'UP';

    return (
        <div className="flex h-screen bg-page text-primary overflow-hidden font-sans">
            {/* Desktop Sidebar */}
            <aside className={cn(
                "hidden lg:flex flex-col bg-surface border-r border-border transition-all duration-250 z-20 shrink-0 relative",
                sidebarOpen ? "w-64" : "w-16" // w-16 = 64px
            )}>
                <NavContent sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} setMobileOpen={setMobileOpen} preferences={preferences} />
            </aside>

            {/* Mobile Sidebar (Drawer) */}
            {mobileOpen && (
                <div className="fixed inset-0 z-40 lg:hidden">
                    <div className="fixed inset-0 bg-page/80 backdrop-blur-sm" onClick={() => setMobileOpen(false)} />
                    <aside className="fixed top-0 left-0 bottom-0 w-64 bg-surface border-r border-border flex flex-col z-50 transform transition-transform">
                        <div className="absolute top-4 right-4">
                            <button onClick={() => setMobileOpen(false)} className="text-secondary hover:text-primary p-1" aria-label="Close navigation">
                                <X size={20} />
                            </button>
                        </div>
                        <NavContent sidebarOpen={true} setSidebarOpen={setSidebarOpen} setMobileOpen={setMobileOpen} isMobileDrawer={true} preferences={preferences} />
                    </aside>
                </div>
            )}

            {/* Main Content Area */}
            <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
                {/* Header */}
                <header className="h-16 shrink-0 bg-page border-b border-border flex items-center justify-between px-4 lg:px-8 z-10 sticky top-0">
                    <div className="flex items-center gap-4">
                        {/* Hamburger Button for Mobile Only */}
                        <button 
                            className="lg:hidden text-secondary hover:text-primary transition-colors focus:outline-none focus:ring-2 focus:ring-accent rounded p-1.5 -ml-1.5"
                            aria-label="Toggle navigation"
                            onClick={() => setMobileOpen(true)}
                        >
                            <Menu size={20} />
                        </button>
                        <h1 className="text-lg font-medium text-primary hidden sm:block">{currentPage}</h1>
                    </div>
                    
                    <div className="flex items-center gap-3 sm:gap-6 text-xs font-mono uppercase text-secondary">
                        <div className="hidden sm:flex items-center gap-2">
                            <span>Backend</span>
                            <div className={cn("flex items-center gap-1.5 px-2 py-1 rounded-md border", isConnected ? "bg-success/10 text-success border-success/20" : "bg-danger/10 text-danger border-danger/20")}>
                                <div className={cn("w-1.5 h-1.5 rounded-full", isConnected ? "bg-success" : "bg-danger")} />
                                {isConnected ? "Online" : "Offline"}
                            </div>
                        </div>
                        <div className="hidden md:flex items-center gap-2">
                            <span>Runtime</span>
                            <span className={cn(
                                preferences.runtimeMode === 'simulation' ? "text-warning" : 
                                preferences.cloudMutations ? "text-danger" : "text-success"
                            )}>
                                {preferences.runtimeMode === 'simulation' ? 'SIMULATION' : 'GOOGLE CLOUD'}
                            </span>
                        </div>
                    </div>
                </header>

                {/* Page Content */}
                <main className="flex-1 overflow-y-auto p-4 lg:p-8">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
