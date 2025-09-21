import { useContext } from "react";
import { FormsContext } from "../context/FormsContext";
import { Navigate } from "react-router-dom";


function ProtectedRoute({children}){
    const { isAuthenticated } = useContext(FormsContext);

    if (!isAuthenticated) {
        return <Navigate to="/login" replace/>;
    }

    return children;
}

export default ProtectedRoute;