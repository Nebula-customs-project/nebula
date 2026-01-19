"use client";

import { useMemo } from "react";
import { PROGRESS_MAX_COST } from "../constants";

/**
 * usePricing Hook
 *
 * Handles pricing calculations and progress percentage.
 * Memoized for performance - only recalculates when dependencies change.
 */
export function usePricing(vehicleConfig, configuration) {
  // Calculate pricing and selected parts count
  const pricingData = useMemo(() => {
    if (!vehicleConfig) {
      return { totalPrice: 0, customizationCost: 0, selectedPartsCount: 0 };
    }

    let total = vehicleConfig.basePrice || 0;
    const selectedParts = [];

    vehicleConfig.categories?.forEach((category) => {
      const selectedPartKey = configuration[category.id];
      const selectedPart = category.parts?.find(
        (part) => part.visualKey === selectedPartKey,
      );
      if (selectedPart) {
        total += selectedPart.cost || 0;
        if (selectedPart.cost > 0) {
          selectedParts.push({ category: category.name, part: selectedPart });
        }
      }
    });

    return {
      totalPrice: total,
      customizationCost: total - (vehicleConfig.basePrice || 0),
      selectedPartsCount: selectedParts.length,
    };
  }, [configuration, vehicleConfig]);

  // Calculate progress percentage
  const progressPercentage = useMemo(() => {
    return Math.round(
      Math.min((pricingData.customizationCost / PROGRESS_MAX_COST) * 100, 100),
    );
  }, [pricingData.customizationCost]);

  return {
    ...pricingData,
    progressPercentage,
  };
}
