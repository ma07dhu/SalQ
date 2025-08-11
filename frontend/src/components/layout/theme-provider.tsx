"use client";

import { FontSizeProvider, useFontSize } from '@/context/font-size-context';
import { useEffect, type ReactNode } from 'react';
import { Toaster } from "@/components/ui/toaster";

function FontSizeApplier({ children }: { children: ReactNode }) {
    const { fontSize } = useFontSize();
    
    useEffect(() => {
        // All rem units will be based on this, scaling the entire UI.
        document.documentElement.style.fontSize = `${fontSize}px`;
    }, [fontSize]);

    return <>{children}</>;
}

export function ThemeProvider({ children }: { children: ReactNode }) {
    return (
        <FontSizeProvider>
            <FontSizeApplier>
                {children}
                <Toaster />
            </FontSizeApplier>
        </FontSizeProvider>
    );
}
