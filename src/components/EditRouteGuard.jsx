import { useContext, useEffect } from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { FormsContext } from '../context/FormsContext';

const EditRouteGuard = ({ children }) => {
  const { id } = useParams();
  const formIdStr = String(id);

  const {
    useFormAccess,
    loadCollaborators,
    getUserIdFromToken,
    collaboratorsByFormId,
  } = useContext(FormsContext);

  const uid = getUserIdFromToken();
  if (!uid) {
    return <Navigate to="/login" replace />;
  }

  const { canAccess, isOwner } = useFormAccess(formIdStr);

  const collabsForForm = collaboratorsByFormId?.[formIdStr];
  const collabsLoading = !isOwner && collabsForForm === undefined;

  useEffect(() => {
    if (collabsLoading) {
      loadCollaborators(Number(id)).catch(() => {});
    }
  }, [collabsLoading, loadCollaborators, id]);

  if (collabsLoading) {
    return <div />;
  }

  if (!canAccess) {
    return <Navigate to="/" replace />;
  }

  return children;
};

export default EditRouteGuard;