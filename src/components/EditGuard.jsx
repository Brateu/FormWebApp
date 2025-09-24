import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FormsContext } from '../context/FormsContext';
import { toast } from 'react-toastify';

const EditGuard = ({ children }) => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { loadForm, loadCollaborators, useFormAccess, ROLE } = useContext(FormsContext);
  const [loading, setLoading] = useState(true);
  const { role, isOwner, canEdit } = useFormAccess(Number(id));

  useEffect(() => {
    (async () => {
      await loadForm(id);
      await loadCollaborators(id); 
      setLoading(false);
    })();
  }, [id]);

  useEffect(() => {
    if (!loading && !canEdit) {
      toast.info('You have view-only access to this form.');
      navigate(`/forms/${id}/preview`, { replace: true });
    }
  }, [loading, canEdit, id]);

  if (loading) return null; 
  return canEdit ? children : null;
};

export default EditGuard
