import { ClipboardList, Eye, Lock, Share, Trash2, UserPlus } from 'lucide-react';
import React, { useContext, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { NavLink } from 'react-router-dom';
import { FormsContext } from '../context/FormsContext';
import { IconButton } from '@mui/material';
import axios from '../context/AxiosInstance';
import { toast } from 'react-toastify';
import CollaboratorsModal from './CollaboratorsModal';

const NewHeader = () => {

    const { form, setForm, navigate, getUserIdFromToken, handleFormShare, useFormAccess } = useContext(FormsContext);
    const [isCollabOpen, setCollabOpen] = useState(false);
    const { id } = useParams();


    const goPreview = () => {
      const targetId = id || form?.id;
        navigate(`/forms/${targetId}/preview`, { state: { useLocal: true }})
    }

  return (
    <div className='flex items-center justify-between py-5 bg-white'>
      <div className='flex items-end gap-5'>
        <NavLink to='/'>
            <ClipboardList size={50} />
        </NavLink>
        <input onChange={(e) => setForm({...form, title: e.target.value})} type="text" value={form.title} placeholder={`${form.title}` || 'Untitled form'} className='outline-none placeholder:text-black placeholder:text-lg border-b-2 border-transparent focus:border-[rgb(103,58,183)] py-2 px-3 w-45'/>
      </div>

      <ul className='hidden sm:flex gap-5 text-lg '>
        <div className='flex flex-col items-center gap-1'>
            <p>Questions</p>
            <hr className='w-1/2 border-none h-[1.5px] bg-gray-700 hidden'/>
        </div>
      </ul>

      <div className='flex items-center gap-3'>
        <IconButton onClick={() => goPreview()}>
            <Eye size={28} color='black'/>
        </IconButton>
      </div>
    </div>
  )
}

export default NewHeader
