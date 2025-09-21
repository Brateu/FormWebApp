import React, { useContext, useState } from 'react'
import { FormsContext } from '../context/FormsContext'
import SingleQFill from './SingleQFill';

const FillList = () => {

    const { form, answers, setAnswers } = useContext(FormsContext);

    const handleAnswerChange = (questionId, value) => {
        setAnswers((prev) => ({
            ...prev,
            [questionId]: value
        }));
    };



  return (
    <div>
      {
        form.questions.map((question) => (
            <SingleQFill
            key={question.id} 
            question={question}
            value={answers[question.id]}
            onChange={(val) => handleAnswerChange(question.id, val)}/>
        ))
      }
    </div>
  )
}

export default FillList
