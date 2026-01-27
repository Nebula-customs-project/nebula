"use client";


import { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "../../hooks/useAuth";
import { authApi } from "../../lib/api";
import { Mail, Lock, User, Phone, Image, Globe, MapPin } from "lucide-react";

export default function RegisterPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [form, setForm] = useState({
    username: "",
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    verifyPassword: ""
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleClearField = (field) => {
    setForm({ ...form, [field]: "" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    // Validation
    if (!form.username || form.username.length < 3) {
      setError("Username must be at least 3 characters.");
      return;
    }
    if (!form.email || !/^\S+@\S+\.\S+$/.test(form.email)) {
      setError("Please enter a valid email address.");
      return;
    }
    if (!form.password || form.password.length < 6) {
      setError("Password must be at least 6 characters.");
      return;
    }
    if (form.password !== form.verifyPassword) {
      setError("Passwords do not match.");
      return;
    }
    setIsLoading(true);
    try {
      const submitForm = { ...form };
      delete submitForm.verifyPassword;
      await authApi.register(submitForm);

      // Auto-login after successful registration
      setSuccess("Registration successful! Logging you in...");

      const loginResult = await authApi.login(form.email, form.password);
      if (loginResult && loginResult.user) {
        // Auto-login success: store user in context/localStorage
        login(loginResult);

        // Redirect based on role
        if (loginResult.user.role === 'ADMIN') {
          router.push('/admin-dashboard');
        } else {
          router.push('/my-nebula-car');
        }
      } else {
        // Fallback: if auto-login fails, redirect to login page
        setSuccess("Registration successful! Please log in.");
        setTimeout(() => router.push("/login"), 1500);
      }
    } catch (err) {
      setError(err.message || "Registration failed.");
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
        <div className="bg-white/10 backdrop-blur-md rounded-2xl shadow-2xl p-8 border border-white/20">
          <h2 className="text-2xl font-bold text-white mb-2">Create Account</h2>
          <p className="text-gray-300 text-sm mb-6">Register to start your Nebula journey</p>

          {error && (
            <div className="mb-6 p-4 bg-red-500/20 border border-red-400/50 text-red-200 rounded-lg text-sm backdrop-blur-sm">
              <div className="flex items-center gap-2">
                <div className="w-2 h-2 bg-red-400 rounded-full"></div>
                {error}
              </div>
            </div>
          )}
          {success && (
            <div className="mb-6 p-4 bg-green-500/20 border border-green-400/50 text-green-200 rounded-lg text-sm backdrop-blur-sm">
              <div className="flex items-center gap-2">
                <div className="w-2 h-2 bg-green-400 rounded-full"></div>
                {success}
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            {/* Username */}
            <div>
              <label className="block text-sm font-medium text-gray-200 mb-2">Username</label>
              <div className="relative flex items-center">
                <User className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-300/50" size={18} />
                <input
                  name="username"
                  value={form.username}
                  onChange={handleChange}
                  placeholder="Username"
                  className="w-full pl-10 pr-10 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none transition text-white placeholder:text-white/40 backdrop-blur-sm"
                  required
                />
                {form.username && (
                  <button type="button" onClick={() => handleClearField('username')} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-red-500 text-lg bg-transparent border-none cursor-pointer">×</button>
                )}
              </div>
            </div>
            {/* First Name & Last Name */}
            <div className="flex gap-2">
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-200 mb-2">First Name</label>
                <div className="relative flex items-center">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-300/50" size={18} />
                  <input name="firstName" value={form.firstName} onChange={handleChange} placeholder="First Name" className="w-full pl-10 pr-10 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none transition text-white placeholder:text-white/40 backdrop-blur-sm" />
                  {form.firstName && (
                    <button type="button" onClick={() => handleClearField('firstName')} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-red-500 text-lg bg-transparent border-none cursor-pointer">×</button>
                  )}
                </div>
              </div>
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-200 mb-2">Last Name</label>
                <div className="relative flex items-center">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-300/50" size={18} />
                  <input name="lastName" value={form.lastName} onChange={handleChange} placeholder="Last Name" className="w-full pl-10 pr-10 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none transition text-white placeholder:text-white/40 backdrop-blur-sm" />
                  {form.lastName && (
                    <button type="button" onClick={() => handleClearField('lastName')} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-red-500 text-lg bg-transparent border-none cursor-pointer">×</button>
                  )}
                </div>
              </div>
            </div>
            {/* Email */}
            <div>
              <label className="block text-sm font-medium text-gray-200 mb-2">Email Address</label>
              <div className="relative flex items-center">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-300/50" size={18} />
                <input
                  name="email"
                  type="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder="Email address"
                  className="w-full pl-10 pr-10 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none transition text-white placeholder:text-white/40 backdrop-blur-sm"
                  required
                />
                {form.email && (
                  <button type="button" onClick={() => handleClearField('email')} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-red-500 text-lg bg-transparent border-none cursor-pointer">×</button>
                )}
              </div>
            </div>
            {/* Password */}
            <div>
              <label className="block text-sm font-medium text-gray-200 mb-2">Password</label>
              <div className="relative flex items-center">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-300/50" size={18} />
                <input
                  name="password"
                  type="password"
                  value={form.password}
                  onChange={handleChange}
                  placeholder="Password"
                  className="w-full pl-10 pr-10 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none transition text-white placeholder:text-white/40 backdrop-blur-sm"
                  required
                />
                {form.password && (
                  <button type="button" onClick={() => handleClearField('password')} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-red-500 text-lg bg-transparent border-none cursor-pointer">×</button>
                )}
              </div>
            </div>
            {/* Verify Password */}
            <div>
              <label className="block text-sm font-medium text-gray-200 mb-2">Verify Password</label>
              <div className="relative flex items-center">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-300/50" size={18} />
                <input
                  name="verifyPassword"
                  type="password"
                  value={form.verifyPassword}
                  onChange={handleChange}
                  placeholder="Re-enter password"
                  className="w-full pl-10 pr-10 py-3 bg-white/10 border border-white/20 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none transition text-white placeholder:text-white/40 backdrop-blur-sm"
                  required
                />
                {form.verifyPassword && (
                  <button type="button" onClick={() => handleClearField('verifyPassword')} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-red-500 text-lg bg-transparent border-none cursor-pointer">×</button>
                )}
              </div>
            </div>

            {/* Register Button */}
            <button
              type="submit"
              disabled={isLoading}
              className="w-full mt-6 bg-red-600 hover:bg-red-700 text-white py-3 rounded-lg font-semibold transition duration-300 disabled:opacity-50 disabled:cursor-not-allowed shadow-lg hover:shadow-red-600/40 disabled:shadow-none"
            >
              {isLoading ? (
                <span className="flex items-center justify-center gap-2">
                  <div className="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
                  Registering...
                </span>
              ) : (
                'Register'
              )}
            </button>
            {/* Login prompt */}
            <div className="mt-6 text-center">
              <span className="text-gray-300 text-sm">Already have an account?</span>
              <a href="/login" className="ml-2 text-red-400 hover:text-red-500 font-semibold transition">Sign In</a>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
