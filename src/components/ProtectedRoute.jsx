import { useContext } from "react";
import { FormsContext } from "../context/FormsContext";
import { Navigate } from "react-router-dom";


function ProtectedRoute({children}){
    const { isAuthenticated, authReady } = useContext(FormsContext);

    if (!authReady) {
        return null;
    }

    return isAuthenticated ? children : <Navigate to="/login" replace />;
}

export default ProtectedRoute;