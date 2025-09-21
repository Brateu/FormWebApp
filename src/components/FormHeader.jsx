import { CircleUser, ClipboardList, EllipsisVertical, Eye, UserPlus } from 'lucide-react'
import React, { useContext } from 'react'
import { NavLink } from 'react-router-dom'
import { FormsContext } from '../context/FormsContext'
import { Button, IconButton } from '@mui/material';

const FormHeader = ({title}) => {

    const { form, setForm } = useContext(FormsContext);

  return (
    <div className='flex items-center justify-between py-5 bg-white'>
      <div className='flex items-end gap-5'>
        <NavLink to='/'>
            <ClipboardList size={50} />
        </NavLink>
        <input onChange={(e) => setForm({...form, title: e.target.value})} type="text" value={title} placeholder='Untitled Form' className='outline-none placeholder:text-black placeholder:text-lg border-b-2 border-transparent focus:border-[rgb(103,58,183)] py-2 px-3 w-45'/>
      </div>

      <ul className='hidden sm:flex gap-5 text-lg '>
        <NavLink to='/forms/new' className='flex flex-col items-center gap-1'>
            <p>Questions</p>
            <hr className='w-1/2 border-none h-[1.5px] bg-gray-700 hidden'/>
        </NavLink>
        <NavLink to='/forms/responses' className='flex flex-col items-center gap-1'>
            <p>Responses</p>
            <hr className='w-1/2 border-none h-[1.5px] bg-gray-700 hidden' />
        </NavLink>
      </ul>

      <div className='flex items-center gap-3'>
        <IconButton>
          <NavLink to='/forms/preview'>
            <Eye size={28} color='black'/>
          </NavLink>
        </IconButton>
        <IconButton>
            <UserPlus size={25} color='black' />
        </IconButton>
        {
          !form.published ? (
            <span onClick={() => setForm({...form, published: true})} className='rounded cursor-pointer hover:bg-[rgb(103,58,200)] bg-[rgb(103,58,183)] text-white px-5 py-1.5'>
              Publish
            </span>
          ) : (
            <span className='border rounded hover:bg-gray-100 cursor-pointer border-[rgb(103,58,183)] text-[rgb(103,58,183)] bg-white px-5 py-1.5'>
              Published
            </span>
          )
        }
        <IconButton>
          <NavLink to='/forms/fill'>
            <EllipsisVertical size={25} color='black' />
          </NavLink>
        </IconButton>
        <CircleUser size={35} color='black' />
      </div>
    </div>
  )
}

export default FormHeader
