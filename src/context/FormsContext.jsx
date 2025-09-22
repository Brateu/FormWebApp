import React, { createContext, useEffect, useState } from 'react'
import { v4 as uuidv4 } from 'uuid';
import { useNavigate } from 'react-router-dom';
import axios from '../context/AxiosInstance';

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
      imageUrl: null,
      options: [
        {text: "Option 1", imageUrl: null},
        {text: "Option 2", imageUrl: null}
      ]
    }, {
      id: uuidv4(),
      text: "Demo Question",
      type: "shortAnswer",
      required: false,
      imageUrl: null,
      options: []
    }, {
      id: uuidv4(),
      text: "Long Question",
      type: "paragraph",
      required: false,
      imageUrl: null,
      options: []
    }, {
      id: uuidv4(),
      text: "Checkboxes",
      type: "checkboxes",
      required: false,
      imageUrl: null,
      options: [
        {text: "Option 1", imageUrl: null},
        {text: "Option 2", imageUrl: null}
      ]
    }, {
      id: uuidv4(),
      text: "Date Question",
      type: "date",
      required: false,
      imageUrl: null,
      options: []
    }, {
      id: uuidv4(),
      text: "Time Question",
      type: "time",
      required: false,
      imageUrl: null,
      options: []
    }]
  })
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [saving, setSaving] = useState(false);
  const navigate = useNavigate();

  const QUESTION_TYPE_MAP = {
    shortAnswer: 'SHORT_TEXT',          
    paragraph: 'LONG_TEXT',
    multipleChoice: 'MULTI_CHOICE',
    checkboxes: 'SINGLE_CHOICE',
    date: 'DATE',
    time: 'TIME',
  };

  const mapFormToDto = (uiForm) => {
    return {
      name: uiForm.title || 'Untitled Form',
      description: uiForm.description || '',
      allowAnonymous: !uiForm.auth,
      responseLimit: 0,
      locked: false,
      status: 'DRAFT',
      visibility: 'PRIVATE',
      questions: (uiForm.questions || []).map((q, idx) => ({
        text: q.text || 'Untitled Question',
        type: QUESTION_TYPE_MAP[q.type] || (q.type ? q.type.toUpperCase() : 'SHORT_TEXT'),
        required: !!q.required,
        orderIndex: idx,
        imageUrl: q.imageUrl || null,
        options: (q.options || []).map((opt) => ({
          text: opt.text || '',
          imageUrl: opt.imageUrl || null,
        })),
      })),
    };
  };

  const createForm = async () => {
    try {
      setSaving(true);
      const payload = mapFormToDto(form);
      const { data } = await axios.post('/api/forms', payload);
      navigate('/')
      return data; 
    } catch (err) {
      console.error('Create form failed', err);
      throw err;
    } finally {
      setSaving(false);
    }
  }

  const fileToDataUrl = (file) => {
    return new Promise((resolve, reject) => {
      if (!file) return resolve(null);
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  } 

  const handleAddQuestion = (afterId) => {

    const newQ = {
      id: uuidv4(),
      text: "Untitled question",
      type: "multipleChoice",
      required: false,
      options: [
        {text: "Option 1", imageUrl: null}
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
    answers, setAnswers,
    fileToDataUrl, createForm, saving
  }
  return (
    <FormsContext.Provider value={value}>
      {props.children}
    </FormsContext.Provider>
  )
}

export default FormsContextProvider
