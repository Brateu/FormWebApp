import { ClipboardList, Eye, Lock, Share, Trash2, UserPlus } from 'lucide-react';
import React, { useContext, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { NavLink } from 'react-router-dom';
import { FormsContext } from '../context/FormsContext';
import { IconButton } from '@mui/material';
import axios from '../context/AxiosInstance';
import { toast } from 'react-toastify';
import CollaboratorsModal from './CollaboratorsModal';

const ResponsesHeader = () => {

    const { form, setForm, navigate, getUserIdFromToken, handleFormShare, useFormAccess } = useContext(FormsContext);
    const [isCollabOpen, setCollabOpen] = useState(false);
    const { id } = useParams();

    const { isOwner, canEdit, canManageCollaborators, canPublish, canLock, canDelete } = useFormAccess(id);

    const questions = canEdit ? `/forms/${id}/edit` : id ? `/forms/${id}/edit` : '/forms/new';

    const goPreview = () => {
      const targetId = id || form?.id;
        navigate(`/forms/${targetId}/preview`, { state: { useLocal: true }})
    }

    const openCollaborators = () => {
      if (!id) {
        toast.warn("Save the form first to add collaborators.");
        return;
      }
      if (!canManageCollaborators) return;
      setCollabOpen(true);
    };

    const handleFormDelete = async () => {
      if (!canDelete) return;
      const userId = getUserIdFromToken();
      if (!userId || !id) return;
      try {
        await axios.delete(`/api/forms/${id}`, { headers: { 'X-User-ID': userId }});
        navigate('/');
      } catch (err) {
        console.error('Delete form failed', err)
      }
    }

    const handleTogglePublish = async () => {
      if (!canPublish) return;
      const userId = getUserIdFromToken();
      if (!userId) return;

      if (!id) {
        toast.warn("Save the form first before publishing.");
        return;
      }

      const nextPublished = !form.published;
      
      const prevForm = form;
      setForm({ ...form, published: nextPublished});

      try {
        const visibility = nextPublished ? 'PUBLIC' : 'PRIVATE';
        await axios.put(`/api/forms/${id}/visibility`, null, {
          params: { visibility },
          headers: { 'X-User-ID': userId }
        })
        toast.success(nextPublished ? 'Form published' : 'Form unpublished');
      } catch (err) {
        console.error('Publish toggle failed.', err);
        setForm(prevForm);
        toast.error('Could not update publish state');
      }
    }

    const handleFormLock = async () => {
      if (!canLock) return;
      const userId = getUserIdFromToken();      
      if (!id) return;

      const nextLocked = !form.locked;
      const prevForm = form;
      setForm({ ...form, locked: nextLocked });

      if (nextLocked) {
        try {
          await axios.put(`/api/forms/${id}/lock`, null, {
            headers: { 'X-User-ID': userId }
          });
          toast.success("Form locked!");
        } catch (err) {
          console.error("Form locking failed.", err);
          setForm(prevForm);
          toast.error("Form locking failed!");
        }
      }

      if (!nextLocked) {
        try {
          await axios.put(`/api/forms/${id}/unlock`, null, {
            headers: { 'X-User-ID': userId}
          });
          toast.success("Form unlocked!");
        } catch (e) {
          console.error("Form unlocking failed.", e);
          setForm(prevForm);
          toast.error("Form unlocking failed!");
        }
      }
    }

    useEffect(() => {
      setCollabOpen(false);
    }, [id]);

  return (
    <div className='flex items-center justify-between py-5 bg-white'>
      <div className='flex items-end gap-5'>
        <NavLink to='/'>
            <ClipboardList size={50} />
        </NavLink>
        <input disabled={true} type="text" placeholder={`${form.title}` || 'Untitled Form'} className='outline-none placeholder:text-black placeholder:text-base border-b-2 border-transparent focus:border-[rgb(103,58,183)] py-2 px-3 w-45'/>
      </div>

      <ul className='hidden sm:flex gap-5 text-lg '>
        <NavLink to={questions} className='flex flex-col items-center gap-1'>
            <p>Questions</p>
            <hr className='w-1/2 border-none h-[1.5px] bg-gray-700 hidden'/>
        </NavLink>
        {id && (
          <NavLink to={`/forms/${id}/responses`} className='flex flex-col items-center gap-1'>
            <p>Responses</p>
            <hr className='w-1/2 border-none h-[1.5px] bg-gray-700 hidden' />
          </NavLink>
        )}
      </ul>

      <div className='flex items-center gap-3'>
        <IconButton onClick={() => goPreview()}>
            <Eye size={28} color='black'/>
        </IconButton>
        {canManageCollaborators && (
          <IconButton onClick={() => openCollaborators()}>
            <UserPlus size={25} color='black' />
          </IconButton>
        )}
        {
          canPublish && (
            !form.published ? (
            <button onClick={() => handleTogglePublish()} className='rounded cursor-pointer hover:bg-[rgb(103,58,200)] bg-[rgb(103,58,183)] text-white px-5 py-1.5'>
              Publish
            </button>
            ) : (
            <button onClick={() => handleTogglePublish()} className='border rounded hover:bg-gray-100 cursor-pointer border-[rgb(103,58,183)] text-[rgb(103,58,183)] bg-white px-5 py-1.5'>
              Published
            </button>
            )
          )
        }
        {id && canDelete && (
          <IconButton onClick={() => handleFormDelete()}>
            <Trash2 size={25} color='black' />
          </IconButton>
        )}
        {form.published && (
          <IconButton onClick={() => handleFormShare(id)}>
            <Share size={25} color='black' />
        </IconButton>
        )}
        {id && canEdit && (
          form.locked ? (
          <IconButton onClick={() => handleFormLock()} >
            <Lock size={25} color='red' />
          </IconButton>
        ) : (
          <IconButton onClick={() => handleFormLock()} >
            <Lock size={25} color='black' />
          </IconButton>
        )
        )}
      </div>
      <CollaboratorsModal open={isCollabOpen} onClose={() => setCollabOpen(false)} formId={Number(id)} />
    </div>
  )
}

export default ResponsesHeader
