"use client";
// This layout disables the Footer for the world-drive route only

import { useEffect } from "react";

export default function WorldDriveLayout({ children }) {
  useEffect(() => {
    // Save previous overflow values
    const prevHtmlOverflow = document.documentElement.style.overflow;
    const prevBodyOverflow = document.body.style.overflow;
    document.documentElement.style.overflow = "hidden";
    document.body.style.overflow = "hidden";
    return () => {
      document.documentElement.style.overflow = prevHtmlOverflow;
      document.body.style.overflow = prevBodyOverflow;
    };
  }, []);
  return <>{children}</>;
}
