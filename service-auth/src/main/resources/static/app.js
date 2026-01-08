const accessTokenKey = "auth.accessToken";
const refreshTokenKey = "auth.refreshToken";

const loginForm = document.getElementById("login-form");
const loginHint = document.getElementById("login-hint");
const accessTokenEl = document.getElementById("access-token");
const refreshTokenEl = document.getElementById("refresh-token");
const logoutButton = document.getElementById("logout");
const serviceOutput = document.getElementById("service-output");
const serviceButtons = document.querySelectorAll("[data-service]");

const updateTokenDisplay = () => {
    const accessToken = localStorage.getItem(accessTokenKey) || "";
    const refreshToken = localStorage.getItem(refreshTokenKey) || "";
    accessTokenEl.textContent = accessToken;
    refreshTokenEl.textContent = refreshToken;
};

const clearTokens = () => {
    localStorage.removeItem(accessTokenKey);
    localStorage.removeItem(refreshTokenKey);
    updateTokenDisplay();
};

const saveTokens = (accessToken, refreshToken) => {
    localStorage.setItem(accessTokenKey, accessToken);
    localStorage.setItem(refreshTokenKey, refreshToken);
    updateTokenDisplay();
};

const login = async (username, password) => {
    const response = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
    });
    if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.error || "登录失败");
    }
    return response.json();
};

const refreshAccessToken = async () => {
    const refreshToken = localStorage.getItem(refreshTokenKey);
    if (!refreshToken) {
        return null;
    }
    const response = await fetch("/auth/refresh", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken }),
    });
    if (!response.ok) {
        return null;
    }
    return response.json();
};

const authorizedFetch = async (url, options = {}) => {
    const accessToken = localStorage.getItem(accessTokenKey);
    const response = await fetch(url, {
        ...options,
        headers: {
            ...(options.headers || {}),
            Authorization: accessToken ? `Bearer ${accessToken}` : "",
        },
    });
    if (response.status !== 401 && response.status !== 403) {
        return response;
    }
    const refreshed = await refreshAccessToken();
    if (!refreshed) {
        clearTokens();
        throw new Error("Token 已过期，请重新登录。");
    }
    saveTokens(refreshed.accessToken, refreshed.refreshToken);
    return fetch(url, {
        ...options,
        headers: {
            ...(options.headers || {}),
            Authorization: `Bearer ${refreshed.accessToken}`,
        },
    });
};

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    loginHint.textContent = "";
    try {
        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;
        const data = await login(username, password);
        saveTokens(data.accessToken, data.refreshToken);
        loginHint.textContent = "登录成功，Token 已保存到 localStorage。";
    } catch (error) {
        loginHint.textContent = error.message;
    }
});

logoutButton.addEventListener("click", () => {
    clearTokens();
    serviceOutput.textContent = "已清除 Token。";
});

serviceButtons.forEach((button) => {
    button.addEventListener("click", async () => {
        serviceOutput.textContent = "请求中...";
        try {
            const response = await authorizedFetch(button.dataset.service);
            const data = await response.json();
            serviceOutput.textContent = JSON.stringify(data, null, 2);
        } catch (error) {
            serviceOutput.textContent = error.message;
        }
    });
});

updateTokenDisplay();
