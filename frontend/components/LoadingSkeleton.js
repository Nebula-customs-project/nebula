"use client";

import React from "react";
import PropTypes from "prop-types";

// Generate stable unique IDs for skeleton items
const generateSkeletonIds = (count, prefix) =>
  new Array(count)
    .fill(null)
    .map((_, i) => `${prefix}-skeleton-${i}`);

/**
 * LoadingSkeleton Component
 *
 * Displays animated loading skeletons for configuration options
 */
export default function LoadingSkeleton({ count = 3 }) {
  const skeletonIds = generateSkeletonIds(count, "option");

  return (
    <div className="space-y-2">
      {skeletonIds.map((id, i) => (
        <div
          key={id}
          className="p-3 rounded-lg border border-gray-700/50 bg-gray-800/30 relative overflow-hidden"
          style={{ animationDelay: `${i * 0.1}s` }}
        >
          {/* Shimmer effect */}
          <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent animate-shimmer"></div>

          <div className="flex items-start justify-between relative z-10">
            <div className="flex-1 space-y-2">
              <div className="h-4 bg-gray-700/50 rounded w-3/4 animate-pulse"></div>
              <div className="h-3 bg-gray-700/30 rounded w-1/2 animate-pulse"></div>
              <div className="h-4 bg-gray-700/40 rounded w-1/4 animate-pulse"></div>
            </div>
            <div className="w-5 h-5 bg-gray-700/50 rounded-full ml-3 animate-pulse"></div>
          </div>
        </div>
      ))}
    </div>
  );
}

LoadingSkeleton.propTypes = {
  count: PropTypes.number,
};

/**
 * CategoryTabSkeleton Component
 *
 * Loading skeleton for category tabs
 */
export function CategoryTabSkeleton() {
  const tabIds = generateSkeletonIds(3, "tab");

  return (
    <div className="bg-gray-900/80 backdrop-blur-sm border-b border-gray-700/50 shadow-md">
      <div className="flex">
        {tabIds.map((id) => (
          <div
            key={id}
            className="shrink-0 px-4 py-3 border-b-2 border-transparent relative overflow-hidden"
          >
            {/* Shimmer effect */}
            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent animate-shimmer"></div>

            <div className="flex items-center gap-2 relative z-10">
              <div className="w-4 h-4 bg-gray-700/50 rounded animate-pulse"></div>
              <div className="h-4 bg-gray-700/50 rounded w-20 animate-pulse"></div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
