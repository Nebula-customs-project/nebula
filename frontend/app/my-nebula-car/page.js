"use client";

import {
  Car,
  Fuel,
  Navigation,
  Wrench,
  Shield,
  Zap,
  Lock,
  Thermometer,
  Play,
} from "lucide-react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { useVehicleTelemetry } from "@/hooks/useVehicleTelemetry";
import { useUserVehicleInfo } from "@/hooks/useUserVehicleInfo";
import { useAuth } from "@/hooks/useAuth";
import { useState, useEffect } from "react";
import dynamic from "next/dynamic";
import TyrePressureDisplay from "./TyrePressureDisplay";

const VehicleMap = dynamic(() => import("./VehicleMap"), {
  ssr: false,
  loading: () => (
    <div className="w-full h-full bg-gradient-to-br from-gray-900 to-gray-950 animate-pulse flex items-center justify-center">
      <div className="flex flex-col items-center gap-3">
        <div className="w-8 h-8 border-2 border-red-500/30 border-t-red-500 rounded-full animate-spin" />
        <span className="text-gray-500 text-sm">Loading Map...</span>
      </div>
    </div>
  ),
});

// Animated Counter Hook
function useAnimatedValue(targetValue, duration = 1500) {
  const [value, setValue] = useState(0);

  useEffect(() => {
    const startTime = Date.now();
    const startValue = 0;

    const animate = () => {
      const elapsed = Date.now() - startTime;
      const progress = Math.min(elapsed / duration, 1);

      // Easing function for smooth animation
      const easeOutQuart = 1 - Math.pow(1 - progress, 4);
      const currentValue =
        startValue + (targetValue - startValue) * easeOutQuart;

      setValue(currentValue);

      if (progress < 1) {
        requestAnimationFrame(animate);
      }
    };

    const timer = setTimeout(() => {
      requestAnimationFrame(animate);
    }, 300); // Delay start for staggered effect

    return () => clearTimeout(timer);
  }, [targetValue, duration]);

  return value;
}

// Animated Circular Progress Ring Component
function CircularGauge({
  value,
  max = 100,
  size = 160,
  strokeWidth = 12,
  color = "#ef4444",
  label,
  sublabel,
  delay = 0,
}) {
  const [mounted, setMounted] = useState(false);
  const animatedValue = useAnimatedValue(value, 2000);

  useEffect(() => {
    const timer = setTimeout(() => setMounted(true), delay);
    return () => clearTimeout(timer);
  }, [delay]);

  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const progress = Math.min(animatedValue / max, 1);
  const offset = circumference - progress * circumference;

  return (
    <div
      className={`relative flex items-center justify-center transition-all duration-700 ${mounted ? "opacity-100 scale-100" : "opacity-0 scale-90"}`}
      style={{ width: size, height: size }}
    >
      {/* Background Ring */}
      <svg className="absolute transform -rotate-90" width={size} height={size}>
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke="rgba(255,255,255,0.05)"
          strokeWidth={strokeWidth}
        />
        {/* Animated Progress Ring */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          fill="none"
          stroke={color}
          strokeWidth={strokeWidth}
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          className="transition-all duration-100 ease-out"
          style={{
            filter: `drop-shadow(0 0 10px ${color}50) drop-shadow(0 0 20px ${color}30)`,
          }}
        />
      </svg>
      {/* Center Text with Counter Animation */}
      <div className="flex flex-col items-center justify-center z-10">
        <span className="text-4xl font-bold text-white tabular-nums">
          {animatedValue.toFixed(0)}
        </span>
        <span className="text-sm text-gray-400 font-medium">{label}</span>
        {sublabel && (
          <span className="text-xs text-gray-500 mt-1">{sublabel}</span>
        )}
      </div>
    </div>
  );
}

// Animated Glass Card Component
function GlassCard({
  children,
  className = "",
  gradient = false,
  glowColor = "rgba(239, 68, 68, 0.1)",
  delay = 0,
}) {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setMounted(true), delay);
    return () => clearTimeout(timer);
  }, [delay]);

  return (
    <div
      className={`relative bg-gradient-to-br from-white/[0.05] to-white/[0.02] backdrop-blur-xl rounded-3xl border border-white/10 overflow-hidden group 
        transition-all duration-700 ease-out hover:border-white/20 hover:shadow-2xl
        ${mounted ? "opacity-100 translate-y-0" : "opacity-0 translate-y-8"}
        ${className}`}
      style={{
        boxShadow: `0 0 40px ${glowColor}, inset 0 0 0 1px rgba(255,255,255,0.05)`,
      }}
    >
      {gradient && (
        <div className="absolute inset-0 bg-gradient-to-br from-red-500/5 via-transparent to-blue-500/5 opacity-0 group-hover:opacity-100 transition-opacity duration-700" />
      )}
      {children}
    </div>
  );
}

// Quick Action Button with Hover Animation
function QuickAction({ icon: Icon, label, active = false }) {
  return (
    <button
      className={`flex flex-col items-center gap-2 px-4 py-3 rounded-2xl transition-all duration-300 transform hover:scale-105 active:scale-95 ${active
        ? "bg-red-500/20 text-red-400 border border-red-500/30 shadow-[0_0_20px_rgba(239,68,68,0.3)]"
        : "bg-white/5 text-gray-400 border border-white/10 hover:bg-white/10 hover:text-white hover:border-white/20"
        }`}
    >
      <Icon
        className={`w-5 h-5 transition-transform duration-300 ${active ? "animate-pulse" : "group-hover:scale-110"}`}
      />
      <span className="text-xs font-medium">{label}</span>
    </button>
  );
}

// Animated Stat Pill Component
function StatPill({
  icon: Icon,
  label,
  value,
  unit,
  color = "text-white",
  delay = 0,
}) {
  const [mounted, setMounted] = useState(false);
  const animatedValue = useAnimatedValue(parseFloat(value) || 0, 1500);

  useEffect(() => {
    const timer = setTimeout(() => setMounted(true), delay);
    return () => clearTimeout(timer);
  }, [delay]);

  return (
    <div
      className={`flex items-center gap-3 px-4 py-2 bg-white/5 rounded-full border border-white/10 
      transition-all duration-500 hover:bg-white/10 hover:border-white/20 hover:scale-105
      ${mounted ? "opacity-100 translate-x-0" : "opacity-0 -translate-x-4"}`}
    >
      <Icon
        className={`w-4 h-4 ${color} transition-transform duration-300 hover:rotate-12`}
      />
      <div className="flex items-baseline gap-1">
        <span className="text-white font-semibold tabular-nums">
          {animatedValue.toFixed(0)}
        </span>
        <span className="text-gray-500 text-xs">{unit}</span>
      </div>
      <span className="text-gray-500 text-xs hidden sm:block">{label}</span>
    </div>
  );
}

// Floating Particles Background
function FloatingParticles() {
  return (
    <div className="fixed inset-0 pointer-events-none overflow-hidden">
      {/* Animated Gradient Orbs */}
      <div
        className="absolute w-[600px] h-[600px] bg-red-500/10 rounded-full blur-[150px]"
        style={{
          top: "20%",
          left: "20%",
          animation: "float1 8s ease-in-out infinite",
        }}
      />
      <div
        className="absolute w-[500px] h-[500px] bg-blue-500/10 rounded-full blur-[150px]"
        style={{
          bottom: "20%",
          right: "20%",
          animation: "float2 10s ease-in-out infinite",
        }}
      />
      <div
        className="absolute w-[400px] h-[400px] bg-purple-500/5 rounded-full blur-[120px]"
        style={{
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          animation: "pulse 6s ease-in-out infinite",
        }}
      />

      {/* Keyframe Animations */}
      <style jsx>{`
        @keyframes float1 {
          0%,
          100% {
            transform: translate(0, 0) scale(1);
          }
          50% {
            transform: translate(30px, -30px) scale(1.1);
          }
        }
        @keyframes float2 {
          0%,
          100% {
            transform: translate(0, 0) scale(1);
          }
          50% {
            transform: translate(-40px, 20px) scale(1.05);
          }
        }
        @keyframes pulse {
          0%,
          100% {
            opacity: 0.5;
            transform: translate(-50%, -50%) scale(1);
          }
          50% {
            opacity: 0.8;
            transform: translate(-50%, -50%) scale(1.2);
          }
        }
      `}</style>
    </div>
  );
}


export default function MyCarPage() {
  const { user, isLoading } = useAuth();
  const router = useRouter();
  const { telemetry, isConnected } = useVehicleTelemetry();
  const { vehicleInfo, loading } = useUserVehicleInfo();
  const [pageLoaded, setPageLoaded] = useState(false);

  useEffect(() => {
    if (!isLoading && !user) {
      router.push("/login");
    }
  }, [user, isLoading, router]);

  useEffect(() => {
    setPageLoaded(true);
  }, []);

  if (isLoading || !user) {
    return <div className="min-h-screen bg-[#030712] flex items-center justify-center">
      <div className="w-8 h-8 border-2 border-red-500/30 border-t-red-500 rounded-full animate-spin" />
    </div>;
  }

  const vehicleName =
    vehicleInfo?.vehicleName || telemetry?.vehicleName || "My Nebula Car";
  const vehicleImage = vehicleInfo?.vehicleImage || null;
  const carLocation = telemetry?.carLocation || null;
  const fuelLevel = telemetry?.fuelLevel || 85;
  const maintenanceDate = vehicleInfo?.maintenanceDueDate || "2026-07-25";

  // Calculate days until maintenance
  const daysUntilMaintenance = maintenanceDate
    ? Math.ceil(
      (new Date(maintenanceDate) - new Date()) / (1000 * 60 * 60 * 24),
    )
    : null;

  // Calculate health score based on tyre pressures and fuel
  const tyrePressures = vehicleInfo?.tyrePressures || {
    frontLeft: 33.6,
    frontRight: 32.5,
    rearLeft: 33.3,
    rearRight: 30.6,
  };
  const avgPressure =
    (tyrePressures.frontLeft +
      tyrePressures.frontRight +
      tyrePressures.rearLeft +
      tyrePressures.rearRight) /
    4;
  const pressureScore = Math.min(
    100,
    Math.max(0, 100 - Math.abs(33 - avgPressure) * 5),
  );
  const healthScore = Math.round((pressureScore + fuelLevel) / 2);

  return (
    <div className="min-h-screen bg-[#030712] text-white pt-24 pb-16 px-4 md:px-8 font-sans">
      {/* Animated Background Effects */}
      <FloatingParticles />

      <div className="max-w-[1600px] mx-auto relative z-10">
        {/* Premium Header with Animation */}
        <div
          className={`flex flex-col md:flex-row md:items-center justify-between gap-6 mb-8 transition-all duration-1000 ${pageLoaded ? "opacity-100 translate-y-0" : "opacity-0 -translate-y-4"}`}
        >
          <div className="flex flex-col gap-3">
            <div className="flex items-center gap-4">
              <h1 className="text-5xl md:text-6xl font-black text-white tracking-tight bg-gradient-to-r from-white via-white to-gray-400 bg-clip-text animate-gradient-x">
                {vehicleName}
              </h1>
              <div
                className={`flex items-center gap-2 px-4 py-2 rounded-full backdrop-blur-md border transition-all duration-500 ${isConnected
                  ? "bg-emerald-500/10 border-emerald-500/30 text-emerald-400"
                  : "bg-amber-500/10 border-amber-500/30 text-amber-400"
                  }`}
              >
                <span
                  className={`w-2 h-2 rounded-full ${isConnected ? "bg-emerald-400" : "bg-amber-400"}`}
                  style={{
                    boxShadow: isConnected
                      ? "0 0 10px #10b981, 0 0 20px #10b981"
                      : "0 0 10px #f59e0b",
                    animation: "pulse-glow 2s ease-in-out infinite",
                  }}
                />
                <span className="text-sm font-medium">
                  {isConnected ? "Connected" : "Connecting..."}
                </span>
              </div>
            </div>

            {/* Animated Quick Stats */}
            <div className="flex flex-wrap gap-2">
              <StatPill
                icon={Zap}
                label="Range"
                value={(fuelLevel * 4).toString()}
                unit="km"
                color="text-yellow-400"
                delay={200}
              />
              <StatPill
                icon={Shield}
                label="Health"
                value={healthScore.toString()}
                unit="%"
                color="text-emerald-400"
                delay={400}
              />
              <StatPill
                icon={Wrench}
                label="Service"
                value={daysUntilMaintenance?.toString() || "0"}
                unit="days"
                color="text-blue-400"
                delay={600}
              />
            </div>
          </div>
        </div>

        {/* Main Bento Grid with Staggered Animations */}
        <div className="grid grid-cols-12 gap-4 md:gap-6 auto-rows-min">
          {/* Hero Car Image - Spans 7 cols */}
          <GlassCard
            className="col-span-12 lg:col-span-7 p-1"
            gradient
            glowColor="rgba(239, 68, 68, 0.15)"
            delay={100}
          >
            <div className="relative aspect-[16/10] rounded-[20px] overflow-hidden bg-gradient-to-br from-gray-900 to-black">
              {/* Animated Gradient Overlay */}
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent z-10" />
              <div className="absolute inset-0 bg-gradient-to-r from-red-500/10 via-transparent to-blue-500/10 opacity-0 group-hover:opacity-100 transition-opacity duration-1000 z-10" />

              {vehicleImage ? (
                <Image
                  src={vehicleImage}
                  alt={vehicleName}
                  fill
                  className="object-cover transition-all duration-1000 group-hover:scale-105 group-hover:brightness-110"
                  unoptimized
                  priority
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-gray-900 to-gray-950">
                  <Car className="w-32 h-32 text-gray-800 animate-pulse" />
                </div>
              )}

              {/* Quick Actions Overlay with Animation */}
              <div className="absolute bottom-6 left-6 right-6 z-20 flex items-center justify-center gap-4">
                <QuickAction icon={Lock} label="Lock" active />
                <QuickAction icon={Thermometer} label="Climate" />
                <QuickAction icon={Play} label="Start" />
              </div>
            </div>
          </GlassCard>

          {/* Fuel Gauge - Spans 5 cols */}
          <GlassCard
            className="col-span-12 sm:col-span-6 lg:col-span-5 p-6"
            gradient
            glowColor="rgba(239, 68, 68, 0.1)"
            delay={200}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-red-500/20 to-orange-500/10 flex items-center justify-center border border-red-500/20">
                  <Fuel className="w-5 h-5 text-red-400" />
                </div>
                <div>
                  <h3 className="text-lg font-bold text-white">Fuel Level</h3>
                  <p className="text-xs text-gray-500">Real-time monitoring</p>
                </div>
              </div>
              <span className="px-3 py-1 bg-red-500/10 border border-red-500/20 rounded-full text-xs text-red-400 font-medium">
                Petrol
              </span>
            </div>

            <div className="flex items-center justify-center py-2">
              <CircularGauge
                value={fuelLevel}
                max={100}
                size={150}
                strokeWidth={12}
                color="#ef4444"
                label="%"
                sublabel={`${(fuelLevel * 4).toFixed(0)} km range`}
                delay={400}
              />
            </div>

            {/* Animated Fuel Bars - Similar to reference image */}
            <div className="mt-4">
              <div className="flex items-end justify-center gap-1 h-16 mb-2">
                {[...Array(10)].map((_, i) => {
                  const barHeight =
                    i < Math.floor(fuelLevel / 10)
                      ? 100
                      : i === Math.floor(fuelLevel / 10)
                        ? (fuelLevel % 10) * 10
                        : 0;
                  const hue = 120 - i * 12; // Green to red gradient
                  return (
                    <div
                      key={i}
                      className="w-3 rounded-sm transition-all duration-500"
                      style={{
                        height: `${Math.max(barHeight, 8)}%`,
                        background:
                          barHeight > 0
                            ? `linear-gradient(to top, hsl(${hue}, 80%, 45%), hsl(${hue}, 90%, 55%))`
                            : "rgba(255,255,255,0.05)",
                        boxShadow:
                          barHeight > 0
                            ? `0 0 10px hsla(${hue}, 80%, 50%, 0.4)`
                            : "none",
                        animation:
                          barHeight > 0
                            ? `fuelPulse 1.5s ease-in-out infinite`
                            : "none",
                        animationDelay: `${i * 0.1}s`,
                      }}
                    />
                  );
                })}
              </div>
              <div className="flex justify-between text-[10px] text-gray-500 px-1">
                <span>0</span>
                <span>50</span>
                <span>100</span>
              </div>
            </div>

            {/* Stats Row */}
            <div className="mt-4 grid grid-cols-3 gap-2">
              <div className="text-center p-2 bg-white/5 rounded-xl border border-white/5">
                <p className="text-lg font-bold text-white">
                  {(fuelLevel * 0.52).toFixed(0)}L
                </p>
                <p className="text-[10px] text-gray-500 uppercase">Current</p>
              </div>
              <div className="text-center p-2 bg-white/5 rounded-xl border border-white/5">
                <p className="text-lg font-bold text-white">52L</p>
                <p className="text-[10px] text-gray-500 uppercase">Tank Size</p>
              </div>
              <div className="text-center p-2 bg-gradient-to-br from-emerald-500/10 to-emerald-500/5 rounded-xl border border-emerald-500/20">
                <p className="text-lg font-bold text-emerald-400">92%</p>
                <p className="text-[10px] text-emerald-500/70 uppercase">
                  Efficiency
                </p>
              </div>
            </div>
          </GlassCard>

          {/* Tyre Pressure - Spans 6 cols */}
          <GlassCard
            className="col-span-12 lg:col-span-6 p-6"
            gradient
            glowColor="rgba(59, 130, 246, 0.1)"
            delay={300}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500/20 to-cyan-500/10 flex items-center justify-center border border-blue-500/20 transition-transform duration-300 hover:scale-110 hover:rotate-12">
                  <Car className="w-5 h-5 text-blue-400" />
                </div>
                <div>
                  <h3 className="text-lg font-bold text-white">
                    Tyre Pressure
                  </h3>
                  <p className="text-xs text-gray-500">All wheels status</p>
                </div>
              </div>
              <span
                className={`px-3 py-1 rounded-full text-xs font-semibold transition-all duration-500 ${avgPressure >= 30 && avgPressure <= 35
                  ? "bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 animate-pulse"
                  : "bg-amber-500/10 text-amber-400 border border-amber-500/20"
                  }`}
              >
                {avgPressure >= 30 && avgPressure <= 35
                  ? "Optimal"
                  : "Check Needed"}
              </span>
            </div>
            <div className="h-[280px]">
              <TyrePressureDisplay
                pressures={tyrePressures}
                lowPressureThreshold={31}
              />
            </div>
          </GlassCard>

          {/* Vehicle Health Score - Spans 3 cols */}
          <GlassCard
            className="col-span-12 sm:col-span-6 lg:col-span-3 p-6"
            gradient
            glowColor="rgba(16, 185, 129, 0.1)"
            delay={400}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-500/20 to-green-500/10 flex items-center justify-center border border-emerald-500/20">
                  <Shield className="w-5 h-5 text-emerald-400" />
                </div>
                <div>
                  <h3 className="text-lg font-bold text-white">Health</h3>
                  <p className="text-xs text-gray-500">Overall status</p>
                </div>
              </div>
            </div>

            <div className="flex items-center justify-center mb-4">
              <CircularGauge
                value={healthScore}
                max={100}
                size={120}
                strokeWidth={8}
                color="#10b981"
                label="%"
                sublabel={
                  healthScore >= 80
                    ? "Excellent"
                    : healthScore >= 60
                      ? "Good"
                      : "Needs Care"
                }
                delay={600}
              />
            </div>

            {/* Component Health Breakdown */}
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-xs text-gray-400">Engine</span>
                <div className="flex items-center gap-2">
                  <div className="w-16 h-1.5 bg-gray-800 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-emerald-500 rounded-full"
                      style={{ width: "95%" }}
                    />
                  </div>
                  <span className="text-xs text-emerald-400 w-8">95%</span>
                </div>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-xs text-gray-400">Battery</span>
                <div className="flex items-center gap-2">
                  <div className="w-16 h-1.5 bg-gray-800 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-emerald-500 rounded-full"
                      style={{ width: "88%" }}
                    />
                  </div>
                  <span className="text-xs text-emerald-400 w-8">88%</span>
                </div>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-xs text-gray-400">Brakes</span>
                <div className="flex items-center gap-2">
                  <div className="w-16 h-1.5 bg-gray-800 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-amber-500 rounded-full"
                      style={{ width: "72%" }}
                    />
                  </div>
                  <span className="text-xs text-amber-400 w-8">72%</span>
                </div>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-xs text-gray-400">Tyres</span>
                <div className="flex items-center gap-2">
                  <div className="w-16 h-1.5 bg-gray-800 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-emerald-500 rounded-full"
                      style={{ width: `${pressureScore}%` }}
                    />
                  </div>
                  <span className="text-xs text-emerald-400 w-8">
                    {pressureScore.toFixed(0)}%
                  </span>
                </div>
              </div>
            </div>
          </GlassCard>

          {/* Maintenance Card - Spans 3 cols */}
          <GlassCard
            className="col-span-12 sm:col-span-6 lg:col-span-3 p-6"
            gradient
            glowColor="rgba(99, 102, 241, 0.1)"
            delay={500}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-500/20 to-purple-500/10 flex items-center justify-center border border-indigo-500/20">
                  <Wrench className="w-5 h-5 text-indigo-400" />
                </div>
                <div>
                  <h3 className="text-lg font-bold text-white">Service</h3>
                  <p className="text-xs text-gray-500">Next maintenance</p>
                </div>
              </div>
            </div>

            <div className="flex items-center justify-center mb-4">
              <CircularGauge
                value={daysUntilMaintenance || 0}
                max={365}
                size={120}
                strokeWidth={8}
                color="#6366f1"
                label="days"
                sublabel={maintenanceDate}
                delay={700}
              />
            </div>

            {/* Upcoming Maintenance Tasks */}
            <div className="space-y-2">
              <div className="flex items-center gap-2 p-2 bg-white/5 rounded-lg border border-white/5">
                <div className="w-2 h-2 rounded-full bg-emerald-500" />
                <span className="text-xs text-gray-300 flex-1">Oil Change</span>
                <span className="text-[10px] text-gray-500">Done</span>
              </div>
              <div className="flex items-center gap-2 p-2 bg-white/5 rounded-lg border border-white/5">
                <div className="w-2 h-2 rounded-full bg-emerald-500" />
                <span className="text-xs text-gray-300 flex-1">
                  Filter Replace
                </span>
                <span className="text-[10px] text-gray-500">Done</span>
              </div>
              <div className="flex items-center gap-2 p-2 bg-indigo-500/10 rounded-lg border border-indigo-500/20">
                <div className="w-2 h-2 rounded-full bg-indigo-500 animate-pulse" />
                <span className="text-xs text-indigo-300 flex-1">
                  Brake Inspection
                </span>
                <span className="text-[10px] text-indigo-400">Upcoming</span>
              </div>
              <div className="flex items-center gap-2 p-2 bg-white/5 rounded-lg border border-white/5 opacity-50">
                <div className="w-2 h-2 rounded-full bg-gray-600" />
                <span className="text-xs text-gray-400 flex-1">
                  Tyre Rotation
                </span>
                <span className="text-[10px] text-gray-500">Pending</span>
              </div>
            </div>
          </GlassCard>

          {/* Live Location Map - Spans 12 cols */}
          <GlassCard
            className="col-span-12 p-1"
            gradient
            glowColor="rgba(59, 130, 246, 0.1)"
            delay={600}
          >
            <div className="p-5 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500/20 to-cyan-500/10 flex items-center justify-center border border-blue-500/20 transition-all duration-300 hover:scale-110">
                  <Navigation
                    className="w-5 h-5 text-blue-400 animate-bounce"
                    style={{ animationDuration: "2s" }}
                  />
                </div>
                <div>
                  <h3 className="text-lg font-bold text-white">
                    Live Location
                  </h3>
                  <p className="text-xs text-gray-500">
                    Real-time GPS tracking
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-2">
                <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping" />
                <span className="w-2 h-2 rounded-full bg-emerald-500 absolute" />
                <span className="text-sm text-gray-400 font-mono ml-2">
                  {carLocation
                    ? `${carLocation.lat.toFixed(4)}°N, ${carLocation.lng.toFixed(4)}°E`
                    : "Acquiring..."}
                </span>
              </div>
            </div>

            <div className="h-[350px] rounded-[20px] overflow-hidden m-1 border border-white/5">
              <VehicleMap location={carLocation} vehicleName={vehicleName} />
            </div>
          </GlassCard>
        </div>
      </div>

      {/* Global Animation Styles */}
      <style jsx global>{`
        @keyframes pulse-glow {
          0%,
          100% {
            box-shadow:
              0 0 5px currentColor,
              0 0 10px currentColor;
          }
          50% {
            box-shadow:
              0 0 15px currentColor,
              0 0 25px currentColor;
          }
        }

        @keyframes gradient-x {
          0%,
          100% {
            background-position: 0% 50%;
          }
          50% {
            background-position: 100% 50%;
          }
        }

        @keyframes fuelPulse {
          0%, 100% { 
            opacity: 1;
            transform: scaleY(1);
          }
          50% { 
            opacity: 0.85;
            transform: scaleY(0.95);
          }
        }

        .animate-gradient-x {
          background-size: 200% 200%;
          animation: gradient-x 3s ease infinite;
        }
      `}</style>
    </div>
  );
}
