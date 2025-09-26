import React, { useContext, useEffect } from 'react'
import { FormsContext } from '../context/FormsContext'
import { ToggleLeft, ToggleRight, Plus } from 'lucide-react';
import QuestionsList from '../components/QuestionsList';
import { IconButton } from '@mui/material';
import NewHeader from '../components/NewHeader';

const CreateNew = () => {

    const { form, setForm, createForm, updateForm, loadForm, saving, handleAddQuestion, startNewForm } = useContext(FormsContext);

  return (
    <div className='bg-purple-100 pb-5'>
      <NewHeader />
      <div className='mx-50 my-3 flex flex-col rounded-lg border-t-8 border-b-2 border-b-[rgb(218,220,224)] border-x-2 border-x-[rgb(218,220,224)] border-[rgb(103,58,183)] bg-white'>
        <input onChange={(e) => setForm({...form, title: e.target.value})} type="text" value={form.title} placeholder='Form Title' className='text-3xl outline-none w-auto min-w-[730px] pb-2 mx-5 my-5 focus:border-b-2 border-[rgb(103,58,183)]' />
        <input onChange={(e) => setForm({...form, description: e.target.value})} type="text" value={form.description} placeholder='Form description' className='text-md  outline-none w-auto min-w-[730px] mx-5 mb-5 focus:border-b-2 border-[rgb(103,58,183)]' />
        <div className='flex flex-row items-center gap-4 pl-5 pb-5'>
          <p>Account Required?</p>
          {
            !form.auth ? (<IconButton> <ToggleLeft onClick={() => setForm({...form, auth: true})} className='cursor-pointer' size={35} color='red' /> </IconButton>) : (<IconButton> <ToggleRight onClick={() => setForm({...form, auth: false})} size={35} color='green' className='cursor-pointer' /> </IconButton>)
          }
        </div>
        <div onClick={() => handleAddQuestion()} className='flex flex-row items-center max-w-[190px] gap-4 pl-5 pb-1 mb-1 cursor-pointer hover:text-[rgb(103,58,183)] '>
          <p>Add Question</p>
          <Plus></Plus>
        </div>
      </div>
      <QuestionsList canEdit={true}/>
      <div className='flex justify-end mx-50'>
        <button
          onClick={() => createForm()}
          disabled={saving}
          className={`mt-2 mb-1 px-5 py-2 rounded text-white ${saving ? 'bg-purple-300 cursor-not-allowed' : 'bg-[rgb(103,58,183)] hover:bg-[rgb(131,58,183)] disabled:cursor-not-allowed disabled:bg-gray-300'}`}
        >
          {saving ? 'Creating...' : 'Create Form'}
        </button>
      </div>
    </div>
  )
}

export default CreateNew
