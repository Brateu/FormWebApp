import React, { createContext, useEffect, useState } from 'react'
import { v4 as uuidv4 } from 'uuid';
import { useNavigate } from 'react-router-dom';

export const FormsContext = createContext();
const FormsContextProvider = (props) => {

  const [activeQuestionId, setActiveQuestionId] = useState(null);
  const [search, setSearch] = useState('');
  const [answers, setAnswers] = useState({});
  const [form, setForm] = useState({
    title: '',
    description: '',
    auth: false,
    published: false,
    questions: [{
      id: uuidv4(),
      text: "Untitled Question",
      type: "multipleChoice",
      required: false,
      image: null,
      options: [
        {text: "Option 1", image: null},
        {text: "Option 2", image: null}
      ]
    }, {
      id: uuidv4(),
      text: "Demo Question",
      type: "shortAnswer",
      required: false,
      image: null,
      options: []
    }, {
      id: uuidv4(),
      text: "Long Question",
      type: "paragraph",
      required: false,
      image: null,
      options: []
    }, {
      id: uuidv4(),
      text: "Checkboxes",
      type: "checkboxes",
      required: false,
      image: null,
      options: [
        {text: "Option 1", image: null},
        {text: "Option 2", image: null}
      ]
    }, {
      id: uuidv4(),
      text: "Date Question",
      type: "date",
      required: false,
      image: null,
      options: []
    }, {
      id: uuidv4(),
      text: "Time Question",
      type: "time",
      required: false,
      image: null,
      options: []
    }
    ]
  })
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const navigate = useNavigate();

  const handleAddQuestion = (afterId) => {

    const newQ = {
      id: uuidv4(),
      text: "Untitled question",
      type: "multipleChoice",
      required: false,
      options: [
        {text: "Option 1", image: null}
      ]
    };

    setForm((prevForm) => {
      const prevQuestions = prevForm.questions;

      if (!afterId) {
        return {
          ...prevForm,
          questions: [...prevQuestions, newQ]
        };
      }
      else {
        const index = prevQuestions.findIndex((q) => q.id === afterId)
        if (index < 0) {
          return {
            ...prevForm,
            questions:[...prevQuestions, newQ]
          };
        }

        const updatedQuestions = [
          ...prevQuestions.slice(0, index + 1),
          newQ,
          ...prevQuestions.slice(index + 1)
        ];

        return {
          ...prevForm,
          questions: updatedQuestions
        };
      }
    });

    setActiveQuestionId(newQ.id);
  }

  const handleDuplicateQuestion = (quest, afterId) => {
    const clone = structuredClone(quest);
    clone.id = uuidv4();

    setForm((prevForm) => {
      const prevQuestions = prevForm.questions;

      if (!afterId){
        return {
          ...prevForm,
          questions: [...prevQuestions, clone]
        };
      }
      else {
        const index = prevQuestions.findIndex((q) => q.id === afterId)
        if (index < 0) {
          return {
            ...prevForm,
            questions: [...prevQuestions, clone]
          };
        }
        
        const updatedQuestions = [
          ...prevQuestions.slice(0, index + 1),
          clone,
          ...prevQuestions.slice(index + 1)
        ];

        return{
          ...prevForm,
          questions: updatedQuestions
        };
      }
    })

    setActiveQuestionId(clone.id);
  }

  const handleUpdateQuestion = (id, updatedFields) => {
    setForm((prevForm) => {
      const updatedQuestions = prevForm.questions.map((q) => q.id === id ? {...q, ...updatedFields} : q);
      return {
        ...prevForm,
        questions: updatedQuestions
      };
    });
  }

  const handleDeleteQuestion = (id) => {
    setForm((prevForm) => {
      const updatedQuestions = prevForm.questions.filter((q) => q.id !== id);
      return {
        ...prevForm,
        questions: updatedQuestions
      };
    });

    setActiveQuestionId((prevActive) => (prevActive === id ? null : prevActive));
  }

  useEffect(() => {
    console.log(answers);
  }, [answers])

  const value = {
    search, setSearch, 
    isAuthenticated, setIsAuthenticated,
    navigate,
    form, setForm,
    activeQuestionId, setActiveQuestionId,
    handleAddQuestion, handleDeleteQuestion, handleUpdateQuestion, handleDuplicateQuestion,
    answers, setAnswers
  }
  return (
    <FormsContext.Provider value={value}>
      {props.children}
    </FormsContext.Provider>
  )
}

export default FormsContextProvider
