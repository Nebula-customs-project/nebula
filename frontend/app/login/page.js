"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "../../hooks/useAuth";
import { authApi } from "../../lib/api";
import { LogIn, Mail, Lock } from "lucide-react";

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      // authApi.login now returns { accessToken, refreshToken, expiresIn, refreshExpiresIn, user }
      const result = await authApi.login(email, password);

      if (result && result.user) {
        // Login hook handles storing user data and setting auth state
        const userData = login(result);

        // Redirect based on role
        if (userData.role === "ADMIN") {
          router.push("/admin-dashboard");
        } else {
          router.push('/my-nebula-car');
        }
      } else {
        setError("Login failed. Please try again.");
      }
    } catch (err) {
      // Parse error message for user-friendly display
      let errorMessage = "Login failed. Please try again.";

      if (err.message) {
        if (
          err.message.includes("401") ||
          err.message.includes("Unauthorized")
        ) {
          errorMessage = "Invalid email or password. Please try again.";
        } else if (err.message.includes("404")) {
          errorMessage =
            "Account not found. Please check your email or register.";
        } else if (
          err.message.includes("Network") ||
          err.message.includes("fetch")
        ) {
          errorMessage = "Unable to connect to server. Please try again later.";
        }
      }

      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-neutral-900 via-black to-neutral-900 flex items-center justify-center px-4 py-12 relative overflow-hidden">
      {/* Animated background elements */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-red-500/15 rounded-full blur-3xl"></div>
        <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-neutral-500/10 rounded-full blur-3xl"></div>
      </div>

      <div className="w-full max-w-md relative z-10">
        {/* Login Card */}
        <div className="bg-white/10 backdrop-blur-md rounded-2xl shadow-2xl p-8 border border-white/20">
          <h2 className="text-2xl font-bold text-white mb-2">Welcome Back</h2>
          <p className="text-gray-300 text-sm mb-6">
            Sign in to access your dashboard
          </p>

          {error && (
            <div className="mb-6 p-4 bg-red-500/20 border border-red-400/50 text-red-200 rounded-lg text-sm backdrop-blur-sm">
              <div className="flex items-center gap-2">
                <div className="w-2 h-2 bg-red-400 rounded-full"></div>
                {error}
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Email */}
            <div>
              <label className="block text-sm font-medium text-gray-200 mb-2">
                Email Address
              </label>
              <div className="relative">
                <Mail
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-300/50"
                  size={18}
                />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="Email address"
                  className="w-full pl-10 pr-4 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none transition text-white placeholder:text-white/40 backdrop-blur-sm"
                  required
                />
              </div>
            </div>

            {/* Password */}
            <div>
              <label className="block text-sm font-medium text-gray-200 mb-2">
                Password
              </label>
              <div className="relative">
                <Lock
                  className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-300/50"
                  size={18}
                />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Password"
                  className="w-full pl-10 pr-4 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none transition text-white placeholder:text-white/40 backdrop-blur-sm"
                  required
                />
              </div>
            </div>

            {/* Login Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full mt-6 bg-red-600 hover:bg-red-700 text-white py-3 rounded-lg font-semibold transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed shadow-lg hover:shadow-red-600/40 disabled:shadow-none"
            >
              {isLoading ? (
                <span className="flex items-center justify-center gap-2">
                  <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                  Logging in...
                </span>
              ) : (
                "Sign In"
              )}
            </button>

            {/* Register prompt */}
            <div className="mt-6 text-center">
              <span className="text-gray-300 text-sm">
                Don't have an account?
              </span>
              <a
                href="/register"
                className="ml-2 text-red-400 hover:text-red-500 font-semibold transition"
              >
                Register
              </a>
            </div>
          </form>
        </div>

        {/* Footer removed for cleaner design */}
      </div>
    </div>
  );
}
