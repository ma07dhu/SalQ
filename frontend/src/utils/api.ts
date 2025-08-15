export async function fetchWithAuth(input: RequestInfo, init?: RequestInit) {
    // Get JWT token from browser storage
    const token = typeof window !== "undefined" ? localStorage.getItem("jwtToken") : null;

    // Merge headers (keep any passed-in ones as well)
    const headers = new Headers(init?.headers);
    if (token) {
        headers.set("Authorization", "Bearer " + token);
    }

    const response = await fetch(input, {
        ...init,
        headers
    });

    // Handle expired/invalid token
    if (response.status === 401) {
        if (typeof window !== "undefined") {
            localStorage.removeItem("jwtToken");
            localStorage.removeItem("role");
            window.location.href = "/login";
        }
        throw new Error("Unauthorized - please login again.");
    }

    return response;
}
