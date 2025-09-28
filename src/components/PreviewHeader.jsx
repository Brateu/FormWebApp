import { ArrowLeft, CircleCheck, CircleMinus, Paperclip } from 'lucide-react'
import React, { useContext } from 'react'
import { NavLink } from 'react-router-dom'
import { FormsContext } from '../context/FormsContext'
import { useParams } from 'react-router-dom'

const PreviewHeader = () => {

    const { form, handleFormShare } = useContext(FormsContext);
    const { id } = useParams();

    const arrowLeft = `/forms/${id}/edit`;

  return (
    <div className='flex flex-row items-center justify-between py-5 bg-white'>
      <div className='flex items-center gap-5'>
        <NavLink to={arrowLeft}>
            <ArrowLeft />
        </NavLink>
        <p className='text-lg'>Preview Mode</p>
      </div>
      {
        form.published ? (
            <div className='flex items-center gap-5 '>
                <CircleCheck color='green' />
                <p className='text-md text-green-700'>Published</p>
                <div onClick={() => handleFormShare(id)} className='flex items-center gap-5 border rounded border-[rgb(218,220,224)] bg-white text-blue-500 px-5 py-1.5 hover:bg-blue-50 cursor-pointer'>
                    <Paperclip />
                    <p className='text-md'>Copy responder link</p>
                </div>
            </div>
        ) : (
            <div className='flex items-center gap-5'>
                <CircleMinus color='gray' />
                <p className='text-lg text-gray-500'>Not Published</p>
            </div>
        )
      }
    </div>
  )
}

export default PreviewHeader
