"use client";

import { useFontSize } from "@/context/font-size-context";
import { Button } from "@/components/ui/button";
import { ZoomIn, ZoomOut } from "lucide-react";

export function FontSizeAdjuster() {
  const { increaseFontSize, decreaseFontSize } = useFontSize();

  return (
    <div className="flex items-center gap-1">
      <Button variant="outline" size="icon" className="h-9 w-9" onClick={decreaseFontSize}>
        <ZoomOut className="h-4 w-4" />
        <span className="sr-only">Decrease font size</span>
      </Button>
      <Button variant="outline" size="icon" className="h-9 w-9" onClick={increaseFontSize}>
        <ZoomIn className="h-4 w-4" />
        <span className="sr-only">Increase font size</span>
      </Button>
    </div>
  );
}
