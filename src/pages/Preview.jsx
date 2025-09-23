import React, { useContext, useEffect } from 'react'
import PreviewHeader from '../components/PreviewHeader'
import { FormsContext } from '../context/FormsContext'
import PreviewList from '../components/PreviewList';
import { useParams } from 'react-router-dom';

const Preview = () => {

  const { form, loadForm } = useContext(FormsContext);
  const { id } = useParams();


  useEffect(() => {
    if (id) {
      loadForm(id).catch(() => {})
    }
  }, [id]);

  return (
    <div className='bg-purple-100 pb-5'>
      <PreviewHeader />
      <div className='mx-70 my-3 rounded-lg border-t-8 border-b-2 border-b-[rgb(218,220,224)] border-x-2 border-x-[rgb(218,220,224)] border-[rgb(103,58,183)] bg-white '>
        <div className='p-5'>
          <p className='mb-2 font-sm text-3xl'>{!form.title ? 'Untitled Form' : form.title}</p>
          <p>{form.description}</p>
          <hr className='text-gray-300 my-3' />
          <p className='text-red-500 text-sm'>* Indicates required question</p>
        </div>
      </div>
      <PreviewList />
    </div>
  )
}

export default Preview
