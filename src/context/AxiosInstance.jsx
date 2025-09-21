import axios from "axios";


const instance = axios.create({
    baseURL: "http://localhost:8080"
});


instance.interceptors.request.use(
  (config) => {
    const isAuthRoute = 
        config.url.includes("api/user/login") ||
        config.url.includes("api/user/register");
    
    if (!isAuthRoute) {
        const token = localStorage.getItem("jwtToken")
        if(token){
            config.headers["Authorization"] = `Bearer ${token}`;
        }
    }
    return config;
  },
  (error) => Promise.reject(error)
);


instance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
        localStorage.removeItem("jwtToken");
        if (window.location.pathname !== "/login") {
            window.location.href = "/login";
        } 
    }
    return Promise.reject(error);
  }
);

export default instance;
