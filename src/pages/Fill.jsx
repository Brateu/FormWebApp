import React, { useContext, useState } from 'react'
import { FormsContext } from '../context/FormsContext'
import FillList from '../components/FillList'


const Fill = () => {

    const { form, answers } = useContext(FormsContext);
    const [errorMessage, setErrorMessage] = useState('');

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

    const handleSubmit = () => {
      for (const q of form.questions) {
        if (q.required) {
          const val = answers[q.id];

          if (!isAnswered(val, q.type)) {
            setErrorMessage("Please answer all required questions!");
            return
          }
        }
      }

      console.log("Your answers: ", answers);
      localStorage.setItem("userAnswers", JSON.stringify(answers));
      alert("Thank you! Your answers have been saved locally! ");
    }

  return (
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
      <div className='flex flex-row items-center gap-3 mt-5 ml-70'>
        <div onClick={() => handleSubmit()} className='w-[90px] bg-[rgb(103,58,183)] px-5 py-2 rounded text-white hover:bg-[rgb(131,58,183)] cursor-pointer'>
          Submit
        </div>
        {
          errorMessage ? (
            <p className='text-red-500'>{errorMessage}</p>
          ) : null
        }
      </div>
    </div>
  )
}

export default Fill
