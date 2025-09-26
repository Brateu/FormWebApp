import React, { useContext} from 'react'
import { FormsContext } from '../context/FormsContext'
import PreviewList from '../components/PreviewList';
import { useLocation, useParams } from 'react-router-dom';
import PreviewNewHeader from '../components/PreviewNewHeader';

const PreviewNew = () => {

  const { form } = useContext(FormsContext);
  const location = useLocation();

  return (
    <div className='bg-purple-100 pb-5'>
      <PreviewNewHeader />
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

export default PreviewNew
