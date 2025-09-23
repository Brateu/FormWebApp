import { CircleUser, ClipboardList, EllipsisVertical, Eye, Lock, Share, Trash2, UserPlus } from 'lucide-react'
import React, { useContext, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { NavLink } from 'react-router-dom'
import { FormsContext } from '../context/FormsContext'
import { Button, IconButton } from '@mui/material';
import axios from '../context/AxiosInstance';
import { toast } from 'react-toastify'

const FormHeader = ({title}) => {

    const { form, setForm, navigate, getUserIdFromToken, handleFormShare } = useContext(FormsContext);
    const { id } = useParams();

    const handleFormDelete = async () => {
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

  return (
    <div className='flex items-center justify-between py-5 bg-white'>
      <div className='flex items-end gap-5'>
        <NavLink to='/'>
            <ClipboardList size={50} />
        </NavLink>
        <input onChange={(e) => setForm({...form, title: e.target.value})} type="text" value={title} placeholder='Untitled Form' className='outline-none placeholder:text-black placeholder:text-lg border-b-2 border-transparent focus:border-[rgb(103,58,183)] py-2 px-3 w-45'/>
      </div>

      <ul className='hidden sm:flex gap-5 text-lg '>
        <NavLink to={`/forms/${id}/edit`} className='flex flex-col items-center gap-1'>
            <p>Questions</p>
            <hr className='w-1/2 border-none h-[1.5px] bg-gray-700 hidden'/>
        </NavLink>
        <NavLink to={`/forms/${id}/responses`} className='flex flex-col items-center gap-1'>
            <p>Responses</p>
            <hr className='w-1/2 border-none h-[1.5px] bg-gray-700 hidden' />
        </NavLink>
      </ul>

      <div className='flex items-center gap-3'>
        <IconButton>
          <NavLink to={`/forms/${id}/preview`}>
            <Eye size={28} color='black'/>
          </NavLink>
        </IconButton>
        <IconButton>
            <UserPlus size={25} color='black' />
        </IconButton>
        {
          !form.published ? (
            <button onClick={() => handleTogglePublish()} className='rounded cursor-pointer hover:bg-[rgb(103,58,200)] bg-[rgb(103,58,183)] text-white px-5 py-1.5'>
              Publish
            </button>
          ) : (
            <button onClick={() => handleTogglePublish()} className='border rounded hover:bg-gray-100 cursor-pointer border-[rgb(103,58,183)] text-[rgb(103,58,183)] bg-white px-5 py-1.5'>
              Published
            </button>
          )
        }
        {id && (
          <IconButton onClick={() => handleFormDelete()}>
            <Trash2 size={25} color='black' />
          </IconButton>
        )}
        {form.published && (
          <IconButton onClick={() => handleFormShare(id)}>
            <Share size={25} color='black' />
        </IconButton>
        )}
        {id && form.locked ? (
          <IconButton onClick={() => handleFormLock()} >
            <Lock size={25} color='red' />
          </IconButton>
        ) : (
          <IconButton onClick={() => handleFormLock()} >
            <Lock size={25} color='black' />
          </IconButton>
        )}
      </div>
    </div>
  )
}

export default FormHeader
