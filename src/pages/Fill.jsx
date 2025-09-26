import React, { useContext, useState, useEffect } from 'react'
import axios from '../context/AxiosInstance';
import { FormsContext } from '../context/FormsContext';
import FillList from '../components/FillList';
import { useParams } from 'react-router-dom';
import { toast } from 'react-toastify';


const Fill = () => {

    const { form, answers, loadForm, getUserIdFromToken, toResposePayload, navigate } = useContext(FormsContext);
    const [errorMessage, setErrorMessage] = useState('');
    const { id } = useParams();

    const isAnswered = (value, questionType) => {
      if (value === null){
        return false;
      }

      if (questionType === 'multipleChoice') {
        return typeof value === 'string' && value.trim() !== '';
      }

      if (questionType === 'checkboxes') {
        return Array.isArray(value) && value.length !== 0;
      }

      if (questionType === 'shortAnswer' || questionType === 'paragraph') {
        return typeof value === 'string' && value.trim() !== '';
      }

      if (questionType === 'date') {
        return value.day && value.month && value.year;
      }

      if (questionType === 'time') {
        return value.hours && value.minutes;
      }

      return true;
    }

    const handleSubmit = async () => {
      const userId = getUserIdFromToken();
      for (const q of form.questions) {
        if (q.required) {
          const val = answers[q.id];

          if (!isAnswered(val, q.type)) {
            toast.warn("Please answer all required questions!")
            return
          } 
        }
      }

      try {
        const payload = toResposePayload(form, answers);
        //console.log(payload);
        await axios.post('/api/responses', payload, {
          headers: { 'X-User-ID': userId }
        })
        
        toast.success("Answers are saved!")
      } catch (err) {
        console.error(err);
        toast.error("Answers aren't saved, please try again.")
      }    
    }

    useEffect(() => {
      if (id) {
        loadForm(id);
      }
    }, [id]);

  return form.published ? (
    <div className='bg-purple-100 pt-3 pb-10'>
      <div className='mx-70 my-3 rounded-lg border-t-8 border-b-2 border-b-[rgb(218,220,224)] border-x-2 border-x-[rgb(218,220,224)] border-[rgb(103,58,183)] bg-white '>
        <div className='p-5'>
          <p className='mb-2 font-sm text-3xl'>{!form.title ? 'Untitled Form' : form.title}</p>
          <p>{form.description}</p>
          <hr className='text-gray-300 my-3' />
          <p className='text-red-500 text-sm'>* Indicates required question</p>
        </div>
      </div>
      <FillList />
      {form.locked && (
        <p className='ml-70 text-red-500 text-sm'>This form is locked, so it cannot be filled!</p>
      )}
      {!getUserIdFromToken() && form.auth && !form.locked && (
        <p onClick={() => navigate('/login')} className='ml-70 text-red-500 text-sm cursor-pointer hover:text-[rgb(103,58,183)]'>You have to be signed in to fill this form!</p>
      )}
      <div className='flex flex-row items-center gap-3 mt-5 ml-70'>
        <button disabled={form.locked || (!getUserIdFromToken() && form.auth)}  onClick={() => handleSubmit()} className='w-[90px] bg-[rgb(103,58,183)] px-5 py-2 rounded text-white hover:bg-[rgb(131,58,183)] cursor-pointer disabled:cursor-not-allowed disabled:bg-gray-300 disabled:hover:bg-gray-300'>
          Submit
        </button>
        {
          errorMessage ? (
            <p className='text-red-500'>{errorMessage}</p>
          ) : null
        }
      </div>
    </div>
  ) : <div className='flex justify-around mt-150 text-2xl'>
    <p>This form isn't published yet, please be patient!</p>
  </div>
}

export default Fill
