"use client";

import { createContext, useContext, useState, useMemo, type ReactNode } from 'react';

const MIN_FONT_SIZE = 12;
const MAX_FONT_SIZE = 20;
const DEFAULT_FONT_SIZE = 15;

type FontSizeContextType = {
  fontSize: number;
  increaseFontSize: () => void;
  decreaseFontSize: () => void;
};

const FontSizeContext = createContext<FontSizeContextType | undefined>(undefined);

export function FontSizeProvider({ children }: { children: ReactNode }) {
  const [fontSize, setFontSize] = useState(DEFAULT_FONT_SIZE);

  const increaseFontSize = () => {
    setFontSize((prevSize) => Math.min(prevSize + 1, MAX_FONT_SIZE));
  };

  const decreaseFontSize = () => {
    setFontSize((prevSize) => Math.max(prevSize - 1, MIN_FONT_SIZE));
  };

  const value = useMemo(() => ({ fontSize, increaseFontSize, decreaseFontSize }), [fontSize]);

  return (
    <FontSizeContext.Provider value={value}>
      {children}
    </FontSizeContext.Provider>
  );
}

export function useFontSize() {
  const context = useContext(FontSizeContext);
  if (context === undefined) {
    throw new Error('useFontSize must be used within a FontSizeProvider');
  }
  return context;
}
